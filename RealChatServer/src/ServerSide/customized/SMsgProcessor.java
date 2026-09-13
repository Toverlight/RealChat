package ServerSide.customized;

import ServerSide.ChatServer;
import ServerSide.ClientHandler;
import ServerSide.ServerManagementFrame;
import ServerSide.customized.enums.protocol.*;
import ServerSide.customized.fileUtils.ImageUtil;
import ServerSide.database.dataItems.AffiliationItem;
import ServerSide.database.dataItems.FriendshipItem;
import ServerSide.database.dataItems.GroupItem;
import ServerSide.database.dataItems.UserItem;
import ServerSide.database.managers.AffiliationManager;
import ServerSide.database.managers.FriendshipManager;
import ServerSide.database.managers.GroupsManager;
import ServerSide.database.managers.UsersManager;

import javax.swing.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 服务器对网络相关消息的处理方法的静态封装
 */
public class SMsgProcessor {
    private static ServerManagementFrame serverManagementFrame = null;
    private static ChatServer chatServer = null;

    public static void setServerManagementFrame(ServerManagementFrame serverManagementFrame) {
        SMsgProcessor.serverManagementFrame = serverManagementFrame;
    }

    public static void setChatServer(ChatServer chatServer) {
        SMsgProcessor.chatServer = chatServer;
    }

    /**
     * 打印log于前台
     * @param log 日志信息
     */
    public static void transferLog(String log) {
        if (serverManagementFrame != null && serverManagementFrame.isListening()) {
            SwingUtilities.invokeLater(() -> {
                long timestamp = System.currentTimeMillis();
                serverManagementFrame.printLog(Dealer.convertTimestampToStringWithoutDate(timestamp) + log);
            });
        }
    }


    /**
     * 处理心跳检测包
     */
    public static void resolveHeartbeat(ClientHandler clientHandler) {
        if (clientHandler.isConnecting()) {
            clientHandler.sendMessage("Get it");
            // TODO ...
        }
    }


    /**
     * 处理异常消息
     * @param msg 异常消息
     */
    public static void resolveException(String[] msg, ClientHandler clientHandler) {

    }

    /**
     * LOGIN 协议：回应登录请求
     * @param flag 0 成功，1 失败
     * @param avatar 头像图片流
     */
    public static void respondLogin(String uid, int flag, int errCode, String avatar, ClientHandler clientHandler) {
        String uidT;
        if (uid == null) uidT = "0";
        else uidT = uid;
        String avatarT;
        if (avatar == null) avatarT = "0";
        else avatarT = avatar;
        String msg = "LOGIN:" + uidT + ":" + flag + ":" + errCode + ":" + avatarT;
        clientHandler.sendMessage(msg);
    }

    /**
     * 处理登录请求消息
     * @param msg 登录请求消息
     */
    public static void resolveLogin(String[] msg, ClientHandler clientHandler) {
        if (msg.length == 3) {
            String uid = msg[1];
            String pwd = msg[2];
            if (!chatServer.hasUserLoggedIn(uid)) {
                UserItem userItem = UsersManager.findUser(uid);
                System.out.println("获取到登录信息：" + userItem.uid + ":" + userItem.nickname + ":" + userItem.email);

                if (userItem != null) {
                    if (pwd.equals(userItem.password)) {
                        String imageBase64 = ImageUtil.encodeImageToBase64("userAvatars/" + uid + ".png");
                        if (imageBase64 == null) {
                            imageBase64 = "0";
                        }
                        respondLogin(uid, 0, LoginErrCode.OK, imageBase64, clientHandler);
                        clientHandler.setUid(uid);
                        transferLog("用户：" + uid + " 已登录 | 当前已登录的人数：" + clientHandler.getLoggedInSum());
                        Timer timer = new Timer(2500, null);
                        timer.setRepeats(false);
                        timer.addActionListener(e -> {
                            chatServer.sendUpdateWithUid(clientHandler, UpdateStatus.ONLINE);
                            chatServer.sendUpdateWithGid("00000", clientHandler.getLoggedInSum());
                        });
                        timer.start();
                    } else {
                        respondLogin(uid, 1, LoginErrCode.PWD_ERR, null, clientHandler);
                    }
                } else {
                    respondLogin(uid, 1, LoginErrCode.UID_ERR, null, clientHandler);
                }
            } else {
                respondLogin(uid, 1, LoginErrCode.USER_HAS_LOGGED_IN, null, clientHandler);
            }
        } else {
            respondLogin(null, 1, LoginErrCode.BAD_REQ, null, clientHandler);
        }
    }

    /**
     * 随机生成验证码，（用到定时器控制有效期）存至clientHandler内。
     * @return 验证码（5 分钟内有效）
     */
    public static String generateTempVerificationCode(int vcOf, ClientHandler clientHandler) {
        char[] randChars = new char[4];
        Random r = new Random();
        for (int i = 0; i < 4; ++i) {
            randChars[i] = (char)(r.nextInt(10) + 48);
        }
        String vcStr = new String(randChars);
        clientHandler.updateProVc(vcOf, vcStr);
        return vcStr;
    }

    /**
     * （注册）以新生成的验证码，向对方邮箱发送一封邮件。
     * @param email 邮箱
     */
    public static void respondRegisterVC(String email, ClientHandler clientHandler) {
        String v = generateTempVerificationCode(VcOf.REGISTER, clientHandler);
        String msg = "RealChat官方: 这是您的注册用验证码：" + v + "，5分钟内有效，请及时使用！";
        QQMailSender.sendMailVC(email, msg);
    }

    /**
     * REGISTER 协议：回复注册请求
     * @param flag 0 成功，1 失败
     */
    public static void respondRegister(int flag, int errCode, ClientHandler clientHandler) {
        String msg = "REGISTER:" + flag + ":" + errCode;
        clientHandler.sendMessage(msg);
    }



    /**
     * 处理注册请求消息
     * @param msg 注册请求消息
     */
    public static void resolveRegister(String[] msg, ClientHandler clientHandler) {
        if (msg.length == 3) {  // 验证码
            String field2 = msg[1];
            String email = msg[2];
            if ("V".equals(field2)) {
                respondRegisterVC(email, clientHandler);
            }
        } else if (msg.length == 6) { // 注册
            String nickname = msg[1];
            String email = msg[2];
            String password = msg[3];
            String verification = msg[4];
            String avatar = msg[5];
            int vcOf = clientHandler.getVcOf();
            String vc = clientHandler.getVc();
            if (vcOf == VcOf.REGISTER) {
                if (verification.equals(vc)) { // 验证通过，作为新记录插入数据库（uid自动分配）
//                    respondRegister(1, RegisterErrCode.EMAIL_OCCUPIED, clientHandler);

                    UserItem userItem = new UserItem();
                    UsersManager.initNextAvailableUid();
//                    userItem.uid = String.valueOf(UsersManager.getNextAvailableUid());
                    userItem.nickname = nickname;
                    userItem.email = email;
                    userItem.password = password;
                    userItem.registerDatetime = LocalDateTime.now();
                    UsersManager.insertUser(userItem);

                    if (!avatar.equals("0")) {
                        // 将该用户上传的头像图片保存至服务器
                        String uid = String.valueOf(userItem.uid);
                        ImageUtil.decodeBase64ToImage(avatar, "userAvatars/" + uid + ".png");
                    }
                    clientHandler.setTempRegUid(userItem.uid);
                    respondRegister(0, RegisterErrCode.OK, clientHandler);
                } else {
                    respondRegister(1, RegisterErrCode.VC_ERR, clientHandler);
                }
            } else {
                respondRegister(1, RegisterErrCode.VC_INE, clientHandler);
            }

        } else {
            respondRegister(1, RegisterErrCode.BAD_REQ, clientHandler);
        }
    }

    /**
     * （找回账户）以新生成的验证码，向对方邮箱发送一封邮件。
     * @param email 邮箱
     */
    public static void respondRetrieveVC(String email, ClientHandler clientHandler) {
        String v = generateTempVerificationCode(VcOf.REGISTER, clientHandler);
        String msg = "RealChat官方: 这是您的找回账户用验证码：" + v + "，5分钟内有效，请及时使用！";
        QQMailSender.sendMailVC(email, msg);
    }

    /**
     * RETRIEVE 协议：回复找回账户请求
     * @param flag 0 成功，1 失败
     */
    public static void respondRetrieve(int flag, int errCode, ClientHandler clientHandler) {
        String msg = "RETRIEVE:" + flag + ":" + errCode;
        clientHandler.sendMessage(msg);
    }

    /**
     * 处理找回账户回应消息
     * @param msg 找回账户回应消息
     */
    public static void resolveRetrieve(String[] msg, ClientHandler clientHandler) {
        if (msg.length == 3) {  // 验证码
            String field2 = msg[1];
            String email = "example@qq.com";
            if ("V".equals(field2)) {
                respondRetrieveVC(email, clientHandler);
            }
        } else if (msg.length == 4) { // 找回账户
            String uid = msg[1];
            String newPwd = msg[2];
            String verification = msg[3];
            int vcOf = clientHandler.getVcOf();
            String vc = clientHandler.getVc();
            if (vcOf == VcOf.RETRIEVE) {
                if (verification.equals(vc)) { // 找回账户成功，修改新密码
                    UserItem userItem = UsersManager.findUser(uid);
                    if (userItem == null) {
                        // 若查无此人，则回复错误信息
                        respondRetrieve(1, RetrieveErrCode.UID_ERR, clientHandler);
                    } else {
                        userItem.password = newPwd;
                        UsersManager.insertUser(userItem);
                        respondRetrieve(0, RetrieveErrCode.OK, clientHandler);
                    }
                } else {
                    respondRetrieve(1, RetrieveErrCode.VC_ERR, clientHandler);
                }
            } else {
                respondRetrieve(1, RetrieveErrCode.VC_INE, clientHandler);
            }
        } else {
            respondRetrieve(1, RetrieveErrCode.BAD_REQ, clientHandler);
        }
    }

    /**
     * FETCH协议：回应抓取请求
     * @param content 内容流
     */
    public static void respondFetch(int dataId, String content, ClientHandler clientHandler) {
        String contentT;
        if (content == null) contentT = "0";
        else contentT = content;
        String msg = "FETCH:" + dataId + ":" + contentT;
        clientHandler.sendMessage(msg);
    }

    public static void respondFetch(int dataId, String otherId, String content, ClientHandler clientHandler) {
        String contentT;
        if (content == null) contentT = "0";
        else contentT = content;
        String msg = "FETCH:" + dataId + ":" + otherId + ":" + contentT;
        clientHandler.sendMessage(msg);
    }

    /**
     * 处理抓取请求。对于多种类型的数据的请求，一个一个包装回复。
     * @param msg 抓取请求
     */
    public static void resolveFetch(String[] msg, ClientHandler clientHandler) {
        if (msg.length == 2 || msg.length == 3) {
            String[] dataIdStrs = msg[1].split("\\|");

            for (String s : dataIdStrs) {
                int dataId = Integer.parseInt(s);

                switch (dataId) {
                    case DataId.NICKNAME:
                        UserItem userItem = UsersManager.findUser(clientHandler.getUid());
                        if (userItem != null) {
                            String nickname = userItem.nickname;
                            System.out.println("返回昵称");
                            respondFetch(dataId, nickname, clientHandler);
                        }

                        break;
                    case DataId.EMAIL:
                        UserItem userItem1 = UsersManager.findUser(clientHandler.getUid());
                        if (userItem1 != null) {
                            String email = userItem1.email;
                            System.out.println("返回邮箱");
                            respondFetch(dataId, email, clientHandler);
                        }
                        break;
                    case DataId.UID:
                        String tempRegUid = clientHandler.getTempRegUid();
                        if (tempRegUid != null) {
                            respondFetch(dataId, tempRegUid, clientHandler);
                            clientHandler.setTempRegUid(null);
                        } else {
                            respondFetch(DataId.REFUSED, null, clientHandler);
                        }
                        break;
                    case DataId.AVATAR:
                        String imageBase64 = ImageUtil.encodeImageToBase64("userAvatars/" +
                                clientHandler.getUid() + ".png");
                        if (imageBase64 == null) {
                            imageBase64 = "0";
                        }
                        respondFetch(dataId, imageBase64, clientHandler);
                        break;
                    case DataId.FRIENDS:
                        System.out.println("执行好友列表返回");
                        List<FriendshipItem> friendshipItems = FriendshipManager.DatabaseConnection.
                                getFriendshipsByUid(clientHandler.getUid());
                        String friendsMsg = "";
                        for (FriendshipItem f : friendshipItems) {
                            String otherUid;
                            if (f.uid1.equals(clientHandler.getUid())){
                                otherUid = f.uid2;
                            } else {
                                otherUid = f.uid1;
                            }
                            UserItem userItem2 = UsersManager.findUser(otherUid);
                            if (userItem2 != null){
                                String uid = userItem2.uid;
                                String nickname = userItem2.nickname;
                                String email = userItem2.email;

                                friendsMsg = friendsMsg.concat(uid + "," + nickname + "," + email + "|");
                            }
                        }
                        respondFetch(dataId, friendsMsg, clientHandler);
                        break;
                    case DataId.GROUPS:
                        System.out.println("执行群组列表返回");
                        List<AffiliationItem> affiliationItems = AffiliationManager.DatabaseConnection.
                                getAffiliationsByUid(Integer.parseInt(clientHandler.getUid()));
                        String groupsMsg = "";
                        for (AffiliationItem a : affiliationItems) {
                            String joinedGid = a.gid;
                            GroupItem groupItem = GroupsManager.getGroupInfo(Integer.parseInt(joinedGid));
                            if (groupItem != null) {
                                String gid = groupItem.gid;
                                String groupName = groupItem.groupName;
                                String peopleNum = String.valueOf(groupItem.peopleNum);

                                groupsMsg = groupsMsg.concat(gid + "," + groupName + "," + peopleNum + "|");
                            }
                        }
                        respondFetch(dataId, groupsMsg, clientHandler);
                        break;
                    case DataId.OTHER_AVATAR:
                        if (msg.length == 3) {
                            String otherId = msg[2];
                            String imgBase64 = ImageUtil.encodeImageToBase64("userAvatars/" + otherId +
                                    ".png");
                            if (imgBase64 == null) {
                                imgBase64 = "0";
                            }
                            respondFetch(dataId, otherId, imgBase64, clientHandler);
                        }
                        break;
                }

            }
        } else {
            respondFetch(DataId.BAD_REQ, null, clientHandler);
        }

    }

    /**
     * 转发消息 私聊
     * @param uid 发送者的 uid
     * @param targetId 对方 id
     */
    public static void relayChatPrivate(String uid, String nickname, String message, String targetId) {
        ClientHandler clientHandler = chatServer.findByUid(targetId);
        String msg = "CHAT:0:0:" + uid + ":" + nickname + ":" + message;
        clientHandler.sendMessage(msg);
    }
    /**
     * 转发消息 群聊
     * @param uid 发送者的 uid
     * @param targetId 群聊 id
     */
    public static void relayChatPublic(String uid, String nickname, String message, String targetId) {
        List<AffiliationItem> affiliationItems = AffiliationManager.DatabaseConnection.
                getAffiliationsByGid(Integer.parseInt(targetId));
        for (AffiliationItem a : affiliationItems) {
            if (a.uid.equals(uid)) continue;
            ClientHandler clientHandler = chatServer.findByUid(a.uid);
            String msg = "CHAT:1:" + a.gid + ":" + uid + ":" + nickname + ":" + message;
            clientHandler.sendMessage(msg);
        }
    }

    /**
     * 处理聊天请求
     * @param msg 聊天信息
     */
    public static void resolveChat(String[] msg, ClientHandler clientHandler) {
        if (msg.length == 5) {
            String flag = msg[1];
            String targetId = msg[2];
            String nickname = msg[3];
            String message = msg[4];

            if (flag.equals("0")) { // 私聊
                relayChatPrivate(clientHandler.getUid(), nickname, message, targetId);
            } else if (flag.equals("1")) { // 群聊
                if (targetId.equals("00000")) {// 公共聊天室
                    chatServer.sendSelfMsgToAll(clientHandler.getUid(), nickname, message);
                    transferLog(nickname + "(" + clientHandler.getUid() + ") 发言：" + message);
                } else { // 一般群聊
                    relayChatPublic(clientHandler.getUid(), nickname, message, targetId);
                }
            }

        }
    }

    /**
     * SEARCH 协议: 回送查找结果（用户）
     * （目前仅提供查找用户的功能）
     */
    public static void respondSearchUser(int flag, int usage, List<UserItem> userItems, List<String> avatars,
                                         ClientHandler clientHandler) {
        String serialized = "";
        for (UserItem userItem : userItems) {
            serialized = serialized.concat(userItem.uid + ",");
            serialized = serialized.concat(userItem.nickname + ",|");
        }
        serialized = serialized.concat(":");
        for (String avatar : avatars) {
            serialized = serialized.concat(avatar + "|");
        }
        String msg = "SEARCH:" + flag + ":" + usage + ":" + serialized + ":";
        clientHandler.sendMessage(msg);
    }

    /**
     * 处理搜索协议信息（目前仅查找成功的情况会回应消息）
     */
    public static void resolveSearch(String[] msg, ClientHandler clientHandler) {
        if (msg.length == 4) {
            String flag = msg[1];
            String id = msg[2];
            String usage = msg[3];
            List<UserItem> userItems = new ArrayList<>();
            List<String> avatars = new ArrayList<>();

            if (flag.equals(String.valueOf(SearchFlag.USER))) {
                UserItem userItem = UsersManager.findUser(id);
                if (userItem != null) {
                    userItems.add(userItem);
                    String avatar = ImageUtil.encodeImageToBase64("userAvatars/" + userItem.uid + ".png");
                    if (avatar != null) {
                        avatars.add(avatar);
                        if (Integer.parseInt(usage) == SearchUsage.MAKE_FRIEND_OR_JOIN_IN) {
                            respondSearchUser(SearchFlag.USER, SearchUsage.MAKE_FRIEND_OR_JOIN_IN, userItems,
                                    avatars, clientHandler);
                        } else {
                            System.out.println("usage error!");
                        }
                    } else {
                        System.out.println("avatar == null !");
                    }
                }

            } else if (flag.equals(String.valueOf(SearchFlag.GROUP))) {
                // TODO 查找群组功能（暂不提供）
            }
        } else {
            // TODO 回应损坏的请求（先定义其错误码）
        }
    }

    /**
     * 处理好友或入群申请信息
     */
    public static void resolveRequest(String[] msg, ClientHandler clientHandler) {
        if (msg.length == 4) { // 简单转发请求
            String head = msg[0];
            int flag = Integer.parseInt(msg[1]);
            String targetId = msg[2];
            String nickname = msg[3];

            if (flag == RequestFlag.USER) {
                ClientHandler toRelay = chatServer.findByUid(targetId);
                if (toRelay != null) {
                    String message = head + ":" + flag + ":" + clientHandler.getUid() + ":" + nickname + ":";
                    toRelay.sendMessage(message);
                }
            }
            // TODO 加入群组的申请转发

        } else if (msg.length == 5) { // 申请批阅的处理
            String head = msg[0];
            int flag = Integer.parseInt(msg[1]);
            String requesterUid = msg[2];
            int option = Integer.parseInt(msg[3]);
            String userItemStr = msg[4];

            if (flag == RequestFlag.USER) {
                ClientHandler toRelay = chatServer.findByUid(requesterUid);
                if (toRelay != null) {
                    if (option == RequestOption.AGREE) { // 同意则添加好友关系到数据库
                        FriendshipManager.DatabaseConnection.insertFriendship(requesterUid, clientHandler.getUid());
                        Timer timer = new Timer(300, null);
                        timer.setRepeats(false);
                        timer.addActionListener(e -> {
                            chatServer.sendUpdateWithUid(toRelay, UpdateStatus.ONLINE);
                            chatServer.sendUpdateWithUid(clientHandler, UpdateStatus.ONLINE);
                        });
                        timer.start();
                    }
                    String message = head + ":" + flag + ":" + clientHandler.getUid() + ":" + option + ":"
                            + userItemStr + ":";
                    toRelay.sendMessage(message);
                }
            }
            // TODO 加入群组的申请批阅处理与转发
        }
        // TODO 损坏的请求反馈
    }


    /**
     * 处理未知类型消息
     * @param msg 未知类型消息
     */
    public static void resolveUnknown(String msg, ClientHandler clientHandler) {
        System.out.println("来自客户端未知类型消息：" + msg);
    }

}
