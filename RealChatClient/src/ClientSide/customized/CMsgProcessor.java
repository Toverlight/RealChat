package ClientSide.customized;

import ClientSide.ChatClient;
import ClientSide.ClientLoginFrame;
import ClientSide.ClientMainFrame;
import ClientSide.customized.enums.protocol.*;
import ClientSide.customized.fileUtils.ImageUtil;
import ClientSide.customized.rePanel.CrCardPanel;
import ClientSide.customized.rePanel.bubble.MessageBlock;
import ClientSide.customized.rePanel.bubble.MessageBubble;
import ClientSide.customized.reWindow.NotificationPopup;
import ClientSide.database.dataItems.*;
import ClientSide.database.managers.NotificationManager;
import ClientSide.database.managers.PrivateChatManager;
import ClientSide.database.managers.PublicChatManager;
import ClientSide.reference.CopeImageUtil;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * 客户端对网络相关消息的处理方法的静态封装
 */
public class CMsgProcessor {
    private static ClientLoginFrame clientLoginFrame = null;
    private static ClientMainFrame clientMainFrame = null;
    private static ChatClient chatClient = null;

    private static BufferedImage officialAvatar = CopeImageUtil.cutHeadImages("Client/res/images/relation/" +
            "userAvatars/000000.png", Dealer.s(34));

    public static void setClientLoginFrame(ClientLoginFrame clientLoginFrame) {
        CMsgProcessor.clientLoginFrame = clientLoginFrame;
    }

    public static void setClientMainFrame(ClientMainFrame clientMainFrame) {
        CMsgProcessor.clientMainFrame = clientMainFrame;
    }

    public static void setChatClient(ChatClient chatClient) {
        CMsgProcessor.chatClient = chatClient;
    }

    public static ChatClient getChatClient() {
        return chatClient;
    }


    /**
     * 发送登录请求
     * @param uid 6位uid
     * @param passwordArray 密码字符数组
     */
    public static void login(String uid, char[] passwordArray) {
        String loginReqMsg = "LOGIN:" + uid + ":" + String.valueOf(passwordArray);
        chatClient.sendOffer(loginReqMsg);
    }

    /**
     * 注册验证码请求
     * @param email 邮箱地址
     */
    public static void registerVC(String email) {
        String registerVCReqMsg = "REGISTER:V:" + email;
        chatClient.sendOffer(registerVCReqMsg);
    }

    /**
     * 发送注册请求
     * @param nickname 昵称
     * @param email 邮箱
     * @param passwordArray 密码字符数组
     * @param verification 验证码
     */
    public static void register(String nickname, String email, char[] passwordArray, String verification,
                                String avatar) {
        String registerReqMsg = "REGISTER:" + nickname + ":" + email + ":" + String.valueOf(passwordArray) +
                ":" + verification + ":" + avatar;
        chatClient.sendOffer(registerReqMsg);
    }

    /**
     * 找回账户验证码请求
     * @param uid 6位uid
     */
    public static void retrieveVC(String uid) {
        String retrieveVCReqMsg = "RETRIEVE:V:" + uid;
        chatClient.sendOffer(retrieveVCReqMsg);
    }

    /**
     * 找回账户请求
     * @param uid 6位uid
     * @param passwordArray 密码字符数组
     * @param verification 验证码
     */
    public static void retrieve(String uid, char[] passwordArray, String verification) {
        String retrieveReqMsg = "RETRIEVE:" + uid + ":" + String.valueOf(passwordArray) + ":" + verification;
        chatClient.sendOffer(retrieveReqMsg);
    }

    /**
     * 使用 FETCH 协议，请求获取某些特定的数据
     * @param dataId 数据标识
     */
    public static void fetch(int... dataId) {
        String dataIdStr = "";
        for (int i = 0; i < dataId.length - 1; ++i) {
            dataIdStr = dataIdStr.concat(dataId[i] + "|");
        }
        if (dataId.length > 0) {
            dataIdStr = dataIdStr.concat(String.valueOf(dataId[dataId.length - 1]));
        }
        String fetchReqMsg = "FETCH:" + dataIdStr;
        chatClient.sendOffer(fetchReqMsg);
    }

    /**
     * 使用 FETCH 协议，请求获取某些特定的数据
     * @param dataId 数据标识
     * @param otherId 另一人的uid
     */
    public static void fetch(int dataId, String otherId) {
        String fetchReqMsg = "FETCH:" + dataId + ":" + otherId;
        chatClient.sendOffer(fetchReqMsg);
    }

    /**
     * 处理异常消息
     * @param msg 异常消息
     */
    public static void resolveException(String[] msg) {
        /*
        if (msg.length < 2) return; // 空内容消息，直接丢弃
        SwingUtilities.invokeLater(() -> {
            if (clientLoginFrame != null) {
                String[] position = msg[1].split("->");
                ShadowLabel statusLabel = clientLoginFrame.getStatusLabel();
                switch (position[0]) {
                    case "RECONNECT":
                        break;
                    case "CONNECT":
                        if (position.length >= 2) {
                            switch (position[1]) {
                                // 登录时连接失败
                                case "TRY_CONNECT_BY_LOGIN":
                                    break;
                            }
                        }
                }
            }
        });
        */
    }

    /**
     * 处理重连接消息
     * @param msg 重连接消息
     */
    public static void resolveReconnected(String[] msg) {

    }

    /**
     * 处理登录回应消息
     * @param msg 登录回应消息
     */
    public static void resolveLogin(String[] msg) {
        if (msg.length != 5) return;
        String uid = msg[1];
        String flag = msg[2];
        String errCode = msg[3];
        String avatar = msg[4];
        if (flag.equals(String.valueOf(LoginErrCode.OK))) { // 登录成功
            chatClient.setUid(uid);
            if (!avatar.equals("0")) {
                ImageUtil.decodeBase64ToImage(avatar, "own/" + uid + ".png");
                SwingUtilities.invokeLater(() -> {
                    clientLoginFrame.changeAvatarLabel();
                    clientMainFrame.updateAvatar();
                });
                Timer timer = new Timer(2000, null);
                timer.setRepeats(false);
                timer.addActionListener(e -> SwingUtilities.invokeLater(() -> {
                    clientMainFrame.addFriendsAndGroupsToItemsThenNewCp();
                    loadPrivateChatRecords();
                    loadPublicChatRecords();
                    loadNotificationChatRecords();
                }));
                timer.start();
            }
            fetch(DataId.NICKNAME, DataId.EMAIL, DataId.FRIENDS, DataId.GROUPS);
            clientLoginFrame.enterMainFrame();
        } else {    // 登录失败
            if (errCode.equals(String.valueOf(LoginErrCode.UID_ERR))) {
                clientLoginFrame.loginErrFeedback("登录失败：不存在的用户");
            } else if (errCode.equals(String.valueOf(LoginErrCode.PWD_ERR))) {
                clientLoginFrame.loginErrFeedback("登录失败：密码错误");
            } else if (errCode.equals(String.valueOf(LoginErrCode.USER_HAS_LOGGED_IN))) {
                clientLoginFrame.loginErrFeedback("登录失败：该用户已经登录");
            } else if (errCode.equals(String.valueOf(LoginErrCode.BAD_REQ))) {
                clientLoginFrame.loginErrFeedback("登录异常：损坏的请求");
            }
        }

    }

    /**
     * 处理注册回应消息
     * @param msg 注册回应消息
     */
    public static void resolveRegister(String[] msg) {
        if (msg.length != 3) return;
        String flag = msg[1];
        String errCode = msg[2];
        if (flag.equals(String.valueOf(RegisterErrCode.OK))) { // 注册成功
            fetch(DataId.UID);
            NotificationPopup.showNotification("注册成功", clientLoginFrame.getNotificationFont());
        } else { // 注册失败
            if (errCode.equals(String.valueOf(RegisterErrCode.VC_ERR))) {
                clientLoginFrame.registerErrFeedback("注册失败：验证码错误");
            } else if (errCode.equals(String.valueOf(RegisterErrCode.VC_INE))) {
                clientLoginFrame.registerErrFeedback("注册失败：验证码已失效");
            } else if (errCode.equals(String.valueOf(RegisterErrCode.EMAIL_OCCUPIED))) {
                clientLoginFrame.registerErrFeedback("注册失败：该邮箱已被注册");
            } else if (errCode.equals(String.valueOf(RegisterErrCode.BAD_REQ))) {
                clientLoginFrame.registerErrFeedback("注册异常：损坏的请求");
            }
        }
    }

    /**
     * 处理找回账户回应消息
     * @param msg 找回账户回应消息
     */
    public static void resolveRetrieve(String[] msg) {
        if (msg.length != 3) return;
        String flag = msg[1];
        String errCode = msg[2];
        if (flag.equals(String.valueOf(RetrieveErrCode.OK))) { // 找回账户成功
            fetch(DataId.AVATAR);
            NotificationPopup.showNotification("账户" + ChatClient.getTempRetUid() + "新密码已设置完成",
                    clientLoginFrame.getNotificationFont());
        } else if (errCode.equals(String.valueOf(RetrieveErrCode.UID_ERR))){
            clientLoginFrame.retrieveErrFeedback("找回账户失败：查无此人");
        } else if (errCode.equals(String.valueOf(RetrieveErrCode.VC_INE))) {
            clientLoginFrame.retrieveErrFeedback("找回账户失败：验证码已失效");
        } else if (errCode.equals(String.valueOf(RetrieveErrCode.VC_ERR))) {
            clientLoginFrame.retrieveErrFeedback("找回账户失败：验证码错误");
        } else if (errCode.equals(String.valueOf(RetrieveErrCode.BAD_REQ))) {
            clientLoginFrame.retrieveErrFeedback("找回账户异常：损坏的请求");
        }
    }

    /**
     * 处理抓取回应
     * @param msg 抓取回应
     */
    public static void resolveFetch(String[] msg) {
        if (msg.length < 3 || msg.length > 4) return;
        int dataId = Integer.parseInt(msg[1]);  // 数据类型标识
        String content = msg[2];    // 数据内容

        switch (dataId) {
            case DataId.REFUSED:
                NotificationPopup.showNotification("获取失败：服务器拒绝传输",
                        clientLoginFrame.getNotificationFont());
                break;
            case DataId.BAD_REQ:
                NotificationPopup.showNotification("获取异常：损坏的请求",
                        clientLoginFrame.getNotificationFont());
                break;
            case DataId.NICKNAME:
                chatClient.setNickname(content);
                Timer timer = new Timer(200, null);
                timer.setRepeats(false);
                String finalContent = content;
                timer.addActionListener(e -> clientLoginFrame.changeWelcomeWords(finalContent));
                timer.start();
                break;
            case DataId.EMAIL:
                chatClient.setEmail(content);
                break;
            case DataId.UID:
                clientLoginFrame.fillUIDPWDAndAvatar(content);
                Timer timerUID = new Timer(2500, null);
                timerUID.setRepeats(false);
                String finalContent1 = content;
                timerUID.addActionListener(e -> NotificationPopup.showNotification("切记：您的UID为 " +
                                finalContent1, clientLoginFrame.getNotificationFont()));
                timerUID.start();
                break;
            case DataId.AVATAR:
                String tempRetUid = ChatClient.getTempRetUid();
                if (tempRetUid != null) {
                    ImageUtil.decodeBase64ToImage(content, "own/" + tempRetUid + ".png");
                    ChatClient.setTempRetUid(null);
                    SwingUtilities.invokeLater(() -> {
                        clientMainFrame.updateAvatar();
                    });
                }
                break;
            case DataId.FRIENDS:
                String[] friends = content.split("\\|");
                if (!friends[0].equals("")) {
                    for (int i = 0; i < friends.length; ++i) {
                        String[] parts = friends[i].split(",");
                        UserItem userItem = new UserItem();
                        userItem.uid = parts[0];
                        userItem.nickname = parts[1];
                        userItem.email = parts[2];
                        chatClient.addFriendItem(userItem);
                        fetch(DataId.OTHER_AVATAR, userItem.uid);   // 获取每个好友的头像
                        System.out.println("userItem:" + userItem.uid + "," + userItem.nickname);
                    }
                }
                break;
            case DataId.GROUPS:
                String[] groups = content.split("\\|");
                if (!groups[0].equals("")) {
                    for (int i = 0; i < groups.length; ++i) {
                        String[] parts = groups[i].split(",");
                        GroupItem groupItem = new GroupItem();
                        groupItem.gid = parts[0];
                        groupItem.groupName = parts[1];
                        groupItem.peopleNum = parts[2];
                        chatClient.addGroupItem(groupItem);
                        System.out.println("groupItem:" + groupItem.gid + "," + groupItem.groupName + "," +
                                groupItem.peopleNum);
                    }
                }
                break;
            case DataId.OTHER_AVATAR:
                if (msg.length == 4) {
                    String otherId = msg[2];
                    content = msg[3];
                    if (!content.equals("0")) {
                        ImageUtil.decodeBase64ToImage(content, "relation/userAvatars/" + otherId + ".png");
                    }
                }
                break;
        }

    }

    /**
     * 处理系统消息
     * @param msg 系统消息
     */
    public static void resolveSystemMessage(String[] msg) {
        if (msg.length != 2) return;
            String content = msg[1];

            if (chatClient.getUid() != null) {  // 仅已登录的用户才处理
                NotificationManager.DatabaseConnection.addNotification("000000", content, chatClient.getUid());

                CrCardPanel nCp = clientMainFrame.getCrCardPanelByName("00000Cp");
                MessageBubble nMb = new MessageBubble(false, content, Dealer.s(200),
                        ClientMainFrame.getNotificationFont());
                ImageIcon avatar = new ImageIcon(officialAvatar);
                long timestamp = System.currentTimeMillis();
                MessageBlock messageBlock = new MessageBlock(avatar, "RealChat官方",
                        Dealer.convertTimestampToStringWithoutDateSS(timestamp), nMb);
                SwingUtilities.invokeLater(() -> {
                    nCp.addMessageBlock(messageBlock);
                    nCp.scrollToBottom();
                });


            }

    }

    /**
     * 处理更新消息
     * 根据 id 长度判断 uid/gid
     */
    public static void resolveUpdate(String[] msg) {
        if (msg.length != 3) return;
        String id = msg[1];
        if (id.length() == 5) { // gid
            String peopleNum = msg[2];
            CrCardPanel gCp = clientMainFrame.getCrCardPanelByName(id + "Cp");
            gCp.setPeopleNum(Integer.parseInt(peopleNum));
            gCp.refreshTitleText();
        } else if (id.length() == 6) { // uid
            String status = msg[2];
            CrCardPanel uCp = clientMainFrame.getCrCardPanelByName(id + "Cp");
            uCp.setStatusAndRefresh(Integer.parseInt(status));
        }
    }

    /**
     * CHAT 协议：发送聊天内容
     * @param flag 0 私聊， 1 群聊
     * @param id 私聊 uid， 群组 gid
     * @param message 内容
     */
    public static void chat(int flag, String id, String message) {
        String msg = "CHAT:" + flag + ":" + id + ":" + chatClient.getNickname() + ":" + message;
        chatClient.sendOffer(msg);

        if (String.valueOf(flag).equals("0")) { // 私聊
            addMessageBlockAndRefresh(id, true, chatClient.getNickname(), message, 0);
            PrivateChatManager.DatabaseConnection.addPrivateChatRecord(chatClient.getUid(),
                    id, message, chatClient.getUid());
        } else { // 群聊
            addMessageBlockAndRefresh(id, chatClient.getUid(), chatClient.getNickname(), message, 0);
            if (id.equals("00000")){
                NotificationManager.DatabaseConnection.addNotification(chatClient.getUid(), message,
                        chatClient.getUid());
            } else {
                PublicChatManager.addPublicChatRecord(id, chatClient.getUid(), message, chatClient.getUid());
            }
        }

    }

    /**
     * 处理 CHAT 协议信息
     */
    public static void resolveChat(String[] msg) {
        if (msg.length != 6) return;
        String flag = msg[1];
        String gid = msg[2];
        String senderUid = msg[3];
        String nickname = msg[4];
        String message = msg[5];

        if (flag.equals("0")) { // 私聊
            if (senderUid != null) {
                ImageIcon imageIcon = new ImageIcon("Client/res/images/relation/userAvatars/" + senderUid
                        + ".png");
                if (imageIcon.getImageLoadStatus() != java.awt.MediaTracker.COMPLETE) {
                    fetch(DataId.OTHER_AVATAR, senderUid);
                }
                addMessageBlockAndRefresh(senderUid, false, nickname, message, 0);
                PrivateChatManager.DatabaseConnection.addPrivateChatRecord(senderUid, chatClient.getUid(),
                        message, chatClient.getUid());
            }
        } else if (flag.equals("1")) { // 群聊
            if (senderUid != null) {
                ImageIcon imageIcon = new ImageIcon("Client/res/images/relation/userAvatars/" + senderUid
                        + ".png");
                if (imageIcon.getImageLoadStatus() != java.awt.MediaTracker.COMPLETE) {
                    fetch(DataId.OTHER_AVATAR, senderUid);
                }
                addMessageBlockAndRefresh(gid, senderUid, nickname, message, 0);
                if (gid.equals("00000")) {
                    NotificationManager.DatabaseConnection.addNotification(senderUid, message, chatClient.getUid());
                } else {
                    PublicChatManager.addPublicChatRecord(gid, senderUid, message, chatClient.getUid());
                }
            }
        }
    }

    /**
     * 添加消息块到对应选项卡并更新显示（私聊）
     * @param uid 对方id
     * @param fromSelf 是否是自己发言
     * @param message 消息
     * @param timestamp 聊天记录时间。0 表示当前系统时间
     */
    public static void addMessageBlockAndRefresh(String uid, boolean fromSelf, String nickname, String message,
                                                 long timestamp) {
        CrCardPanel nCp = clientMainFrame.getCrCardPanelByName(uid + "Cp"); // id + "Cp"命名容器
        if (timestamp == 0) {
            timestamp = System.currentTimeMillis();
        }
        makeBubbleBlock(uid, fromSelf, nickname, message, nCp, timestamp);
    }

    /**
     * 添加消息块到对应选项卡并更新显示（群聊）
     * @param gid 群组 id
     * @param senderUid 发送者 id，是自己时传自己的 id
     * @param message 消息
     * @param timestamp 聊天记录时间。0 表示当前系统时间
     */
    public static void addMessageBlockAndRefresh(String gid, String senderUid, String nickname, String message,
                                                 long timestamp) {
        CrCardPanel nCp = clientMainFrame.getCrCardPanelByName(gid + "Cp"); // id + "Cp"命名容器
        if (timestamp == 0) {
            timestamp = System.currentTimeMillis();
        }
        boolean fromSelf = senderUid.equals(chatClient.getUid());
        makeBubbleBlock(senderUid, fromSelf, nickname, message, nCp, timestamp);
    }

    private static void makeBubbleBlock(String senderUid, boolean fromSelf, String nickname, String message,
                                        CrCardPanel nCp, long timestamp) {
        MessageBubble nMb = new MessageBubble(fromSelf, message, Dealer.s(200),
                ClientMainFrame.getNotificationFont());
        String subFolderFilename;
        if (fromSelf) {
            subFolderFilename = "own/" + chatClient.getUid();
        } else {
            subFolderFilename = "relation/userAvatars/" + senderUid;
        }
        BufferedImage bf = CopeImageUtil.cutHeadImages("Client/res/images/" + subFolderFilename + ".png",
                Dealer.s(34));
        ImageIcon avatar = new ImageIcon(bf);
        SwingUtilities.invokeLater(() -> {
            MessageBlock messageBlock = new MessageBlock(avatar, nickname,
                Dealer.convertTimestampToStringWithoutDateSS(timestamp), nMb);
            nCp.addMessageBlock(messageBlock);
            nCp.scrollToBottom();
        });
    }

    /**
     * 加载通知（在线聊天室）信息并显示
     */
    public static void loadNotificationChatRecords() {
        List<NotificationItem> notificationItems = NotificationManager.DatabaseConnection.
                getNotifications(chatClient.getUid());
        for (NotificationItem n : notificationItems) {
            String nickname;
            if (n.speakerUid.equals(chatClient.getUid())) {
                nickname = chatClient.getNickname();
            } else if (n.speakerUid.equals("000000")){
                nickname = "RealChat官方";
            } else {
                nickname = "UID:" + n.speakerUid;
            }
            addMessageBlockAndRefresh("00000", n.speakerUid, nickname, n.message,
                    n.nDatetime.toInstant(ZoneOffset.of("+8")).toEpochMilli());
        }
    }

    /**
     * 加载私聊消息并显示
     * 前提是好友已经加载完毕 且 选项卡创建完毕
     */
    public static void loadPrivateChatRecords() {
        System.out.println("加载私聊消息并显示");
        List<UserItem> friends = chatClient.getFriends();
        for (UserItem u : friends) {
            List<PrivateChatItem> privateChatItems = PrivateChatManager.DatabaseConnection.getPrivateChatRecords(
                    chatClient.getUid(), u.uid);
            for (PrivateChatItem p : privateChatItems) {
                boolean fromSelf = p.senderUid.equals(chatClient.getUid());
                String nickname;
                if (fromSelf) {
                    nickname = chatClient.getNickname();
                } else {
                    nickname = u.nickname;
                }
                addMessageBlockAndRefresh(u.uid, fromSelf, nickname, p.message,
                        p.prDatetime.toInstant(ZoneOffset.of("+8")).toEpochMilli());
            }
        }
    }

    /**
     * 加载群聊消息并显示
     * 前提是群聊已经加载完毕 且 选项卡创建完毕
     */
    public static void loadPublicChatRecords() {
        System.out.println("加载群聊消息并显示");
        List<GroupItem> groupItems = chatClient.getGroups();
        for (GroupItem g : groupItems) {
            List<PublicChatItem> publicChatItems = PublicChatManager.getPublicChatRecords(g.gid, chatClient.getUid());
            for (PublicChatItem p : publicChatItems) {
                addMessageBlockAndRefresh(g.gid, p.senderUid, "UID:" + p.senderUid , p.message,
                        p.puDatetime.toInstant(ZoneOffset.of("+8")).toEpochMilli());
            }
        }
    }

    /**
     * SEARCH 协议：搜索用户或群组
     * @param flag SearchFlag常量
     * @param id uid/gid
     * @param usage SearchUsage常量
     */
    public static void search(int flag, String id, int usage) {
        String msg = "SEARCH:" + flag + ":" + id + ":" + usage + ":";
        chatClient.sendOffer(msg);
    }

    /**
     * 处理 SEARCH 协议信息
     */
    public static void resolveSearch(String[] msg) {
        if (msg.length != 5) return;
        String flag = msg[1];
        String usage = msg[2];
        String itemsStr = msg[3];
        String avatarsStr = msg[4];
        if (itemsStr.equals("")) return;
        String[] items = itemsStr.split("\\|");
        List<UserItem> userItems = new ArrayList<>();
        List<GroupItem> groupItems = new ArrayList<>();
        String[] avatars = avatarsStr.split("\\|");

        if (flag.equals(String.valueOf(SearchFlag.USER))) {
            for (int i = 0; i < items.length; ++i) {
                UserItem userItem = parseUserItem(items[i]);
                if (userItem != null) {
                    userItems.add(userItem);
                    if (i < avatars.length && !"".equals(avatars[i])) {
                        ImageUtil.decodeBase64ToImage(avatars[i], "relation/userAvatars/" + userItem.uid +
                                ".png");
                    }
                }
            }
        } else if (flag.equals(String.valueOf(SearchFlag.GROUP))) {
            for (int i = 0; i < items.length; ++i) {
                GroupItem groupItem = parseGroupItem(items[i]);
                if (groupItem != null) {
                    groupItems.add(groupItem);
                    if (i < avatars.length && !"".equals(avatars[i])) {
                        ImageUtil.decodeBase64ToImage(avatars[i], "relation/userAvatars/" + groupItem.gid +
                                ".png");
                    }
                }
            }
        }

        if (usage.equals(String.valueOf(SearchUsage.MAKE_FRIEND_OR_JOIN_GROUP))) {
            System.out.println("usage tick!");
            SwingUtilities.invokeLater(() -> clientMainFrame.getMakeFriendDialog().
                    batchAddUsersAndGroups(userItems, groupItems));
        }
    }

    /**
     * 解析用户数据项，返回UserItem对象。（仅含uid和nickname）
     * @param userItemStr 用户数据项序列化字符串。若为空或null，返回null。
     * @return 用户数据项。
     */
    private static UserItem parseUserItem(String userItemStr) {
        UserItem userItem = new UserItem();
        if (!"".equals(userItemStr) && userItemStr != null) {
            String[] fields = userItemStr.split(",");
            userItem.uid = fields[0];
            userItem.nickname = fields[1];
        } else {
            userItem = null;
        }
        return userItem;
    }

    /**
     * 解析群组数据项，返回GroupItem对象。
     * @param groupItemStr 群组数据项序列化字符串。若为空或null，返回null。
     * @return 群组数据项。
     */
    private static GroupItem parseGroupItem(String groupItemStr) {
        GroupItem groupItem = new GroupItem();
        if (!"".equals(groupItemStr) && groupItemStr != null) {
            String[] fields = groupItemStr.split(",");
            groupItem.gid = fields[0];
            groupItem.groupName = fields[1];
            groupItem.peopleNum = fields[2];
        } else {
            groupItem = null;
        }
        return groupItem;
    }

    /**
     * REQUEST协议：发起在线好友申请
     * @param flag RequestFlag常量
     * @param targetId 对方 uid/gid
     */
    public static void request(int flag, String targetId) {
        String msg = "REQUEST:" + flag + ":" + targetId + ":" + chatClient.getNickname();
        chatClient.sendOffer(msg);
    }

    /**
     * 处理 REQUEST 协议信息。
     * @param msg 信息
     */
    public static void resolveRequest(String[] msg) {
        if (msg.length < 4 || msg.length > 5) return;
        if (msg.length == 4) {  // 申请处理
            int flag = Integer.parseInt(msg[1]);
            String requesterUid = msg[2];
            String nickname = msg[3];
            Object[] options = {"同意", "拒绝"};
            UserItem friend = new UserItem();

            if (flag == RequestFlag.USER) {
                SwingUtilities.invokeLater(() -> {
                    // 显示对话框并获取用户选择
                    int option = JOptionPane.showOptionDialog(
                            null,                // 父组件
                            "你是否同意添加" + nickname + "(" + requesterUid + ")" + "为好友？", // 提示信息
                            "好友申请确认",                     // 标题
                            JOptionPane.YES_NO_OPTION,         // 默认按钮类型
                            JOptionPane.QUESTION_MESSAGE,      // 图标类型
                            null,                         // 自定义图标
                            options,                           // 按钮选项
                            options[0]                        // 默认选中的按钮
                    );

                    if (option == JOptionPane.YES_OPTION) {
                        System.out.println("同意了好友申请");
                        NotificationPopup.showNotification("你已同意好友申请",
                                ClientMainFrame.getNotificationFont());
                        friend.uid = requesterUid;
                        friend.nickname = nickname;
                        clientMainFrame.addFriendToItemListAndNewCp(friend);
                        UserItem ownUserItem = new UserItem();
                        ownUserItem.uid = chatClient.getUid();
                        ownUserItem.nickname = chatClient.getNickname();
                        reviewRequest(flag, requesterUid, RequestOption.AGREE, ownUserItem);
                    } else if (option == JOptionPane.NO_OPTION) {
                        System.out.println("拒绝了好友申请");
                        NotificationPopup.showNotification("你已拒绝好友申请",
                                ClientMainFrame.getNotificationFont());
                        reviewRequest(flag, requesterUid, RequestOption.REFUSE, null);
                    }
                });
            }
            // TODO 入群申请处理

        } else { // 批阅回复的处理
            resolveReviewRequest(msg);
        }
    }

    /**
     * REQUEST协议：批阅好友申请并回复。若不提供 userItem（拒绝），传 null。
     * @param flag RequestFlag常量
     * @param requesterUid 发起好友（入群）申请的用户的uid
     * @param option RequestOption常量
     * @param userItem 用户数据项
     */
    public static void reviewRequest(int flag, String requesterUid, int option, UserItem userItem) {
        String msg = "REQUEST:" + flag + ":" + requesterUid + ":" + option + ":" + serializeUserItem(userItem);
        chatClient.sendOffer(msg);
    }

    /**
     * 处理 REQUEST协议批示信息。
     * @param msg 信息
     */
    private static void resolveReviewRequest(String[] msg) {
        if (msg.length != 5) return;
        int flag = Integer.parseInt(msg[1]);
        // String targetId = msg[2];
        int option = Integer.parseInt(msg[3]);
        UserItem userItem = parseUserItem(msg[4]);

        if (flag == RequestFlag.USER) {
            if (option == RequestOption.AGREE) {
                if (userItem != null) {
                    SwingUtilities.invokeLater(() -> {
                        clientMainFrame.addFriendToItemListAndNewCp(userItem);
                        NotificationPopup.showNotification(userItem.nickname + "(" + userItem.uid + ")" +
                                "同意添加你为好友", ClientMainFrame.getNotificationFont());
                    });
                } else {
                    SwingUtilities.invokeLater(() ->  NotificationPopup.
                            showNotification("对方同意添加好友，但接收数据丢失，请尝试刷新！",
                            ClientMainFrame.getNotificationFont()));

                }
            } else if (option == RequestOption.REFUSE) {
                SwingUtilities.invokeLater(() -> NotificationPopup.showNotification("对方拒绝添加好友！",
                        ClientMainFrame.getNotificationFont()));

            }
        }
        // TODO 入群批示处理
    }

    /**
     * 序列化用户数据项（规定以 , 号分隔字段）。后面不带 | 号。若参数为 null，则返回 ""。
     * @param userItem 用户数据项
     * @return 用户数据项序列
     */
    public static String serializeUserItem(UserItem userItem) {
        if (userItem == null) return "";
        String uid = userItem.uid == null ? "" : userItem.uid;
        String nickname = userItem.nickname == null ? "" : userItem.nickname;
        String email = userItem.email == null ? "" : userItem.email;
        return uid + "," + nickname + "," + email + ",";
    }

    /**
     * 处理未知类型消息
     * @param msg 未知类型消息
     */
    public static void resolveUnknown(String msg) {
        System.out.println("来自服务器未知类型消息：" + msg);
    }
}
