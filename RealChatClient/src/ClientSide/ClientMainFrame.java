package ClientSide;

import ClientSide.customized.CMsgProcessor;
import ClientSide.customized.Dealer;
import ClientSide.customized.reListener.HintFocusListener;
import ClientSide.customized.rePanel.CrCardPanel;
import ClientSide.customized.rePanel.infoItem.InfoItem;
import ClientSide.customized.rePanel.infoItem.InfoList;
import ClientSide.customized.reWindow.NotificationPopup;
import ClientSide.database.dataItems.GroupItem;
import ClientSide.database.dataItems.UserItem;
import ClientSide.reference.CopeImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClientMainFrame extends JFrame {
    private MakeFriendDialog makeFriendDialog = null;

    private Font clientFont = new Font("黑体", Font.PLAIN, 10);;

    private Map<String, CrCardPanel> crCardPanelMap = new HashMap<>();

    private Container container;

    private JPanel menuPanel;
    private JPanel touchCardPanel;
    private JPanel chatRoomPanel;

    private CardLayout crCardLayout;

    private JPanel tcHeadBar;
    private JPanel scrollPanel;

    private JLabel avatarLabel;
    private JRadioButton msgBtn;
    private JRadioButton contactBtn;
    private JRadioButton notificationBtn;
    private JButton mailBtn;
    private JButton folderBtn;
    private ButtonGroup mainMenuBtnGp;
    private CardLayout cardLayoutTc;
    private JPanel msgPanel;
    private JPanel contactPanel;
    private JPanel notificationPanel;

    private InfoList msgInfoList;

    private BufferedImage bf; // 头像缓存


    private static final Font bubbleFont = new Font("思源黑体", Font.PLAIN, Dealer.s(9));
    private static final Font infoItemFont = new Font("微软雅黑", Font.PLAIN, 10);
    private static final Font textEditedStyleFont = new Font("微软雅黑", Font.PLAIN, Dealer.s(10));
    private static final Font notificationFont = new Font("黑体", Font.PLAIN, Dealer.s(10));

    public ClientMainFrame(String title){
        makeFriendDialog = new MakeFriendDialog(this, "添加好友或群聊");
        setTitle(title);

        container = getContentPane();
        container.setBackground(Color.LIGHT_GRAY);
        container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));

        menuPanel = new JPanel();
        menuPanel.setBackground(new Color(206, 206, 206));
        menuPanel.setPreferredSize(new Dimension(Dealer.s(40), Dealer.s(400)));
        menuPanel.setMaximumSize(new Dimension(Dealer.s(40), Integer.MAX_VALUE));
        menuPanel.setMinimumSize(new Dimension(Dealer.s(40), Dealer.s(400)));
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));

        touchCardPanel = new JPanel();
        touchCardPanel.setBackground(new Color(240, 240, 240));
        touchCardPanel.setPreferredSize(new Dimension(Dealer.s(140), Dealer.s(400)));
        touchCardPanel.setMaximumSize(new Dimension(Dealer.s(300), Integer.MAX_VALUE));
        touchCardPanel.setMinimumSize(new Dimension(Dealer.s(140), Dealer.s(400)));
        touchCardPanel.setLayout(new BoxLayout(touchCardPanel, BoxLayout.Y_AXIS));


        chatRoomPanel = new JPanel();
        chatRoomPanel.setBackground(new Color(242, 242, 242));
        chatRoomPanel.setPreferredSize(new Dimension(Dealer.s(320), Dealer.s(400)));
        crCardLayout = new CardLayout();
        chatRoomPanel.setLayout(crCardLayout);

        container.add(menuPanel);
        container.add(touchCardPanel);
        container.add(chatRoomPanel);

        //-

        avatarLabel = new JLabel();
        avatarLabel.setHorizontalAlignment(SwingConstants.CENTER);
        avatarLabel.setVerticalAlignment(SwingConstants.CENTER);
        Dealer.setTripleSizes(avatarLabel, Dealer.s(40), Dealer.s(40));

        avatarLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                NotificationPopup.showNotification("您的 UID:" + CMsgProcessor.getChatClient().getUid() +
                        " 昵称：" + CMsgProcessor.getChatClient().getNickname() + " 邮箱：" +
                        CMsgProcessor.getChatClient().getEmail(), notificationFont);
            }
        });


        msgBtn = new JRadioButton(new ImageIcon("Client/res/images/icons/tablerMsgIdle.png"));
        msgBtn.setHorizontalAlignment(SwingConstants.CENTER);
        msgBtn.setVerticalAlignment(SwingConstants.CENTER);
        Dealer.setTripleSizes(msgBtn, Dealer.s(40), Dealer.s(40));
        msgBtn.setRolloverIcon(new ImageIcon("Client/res/images/icons/tablerMsgRollover.png"));
        msgBtn.setSelectedIcon(new ImageIcon("Client/res/images/icons/tablerMsgSelected.png"));
        Dealer.setButtonTransparent(msgBtn);

        msgBtn.addActionListener(e -> {
            cardLayoutTc.show(scrollPanel, "msgPanel");
        });


        contactBtn = new JRadioButton(new ImageIcon("Client/res/images/icons/fillContactIdle.png"));
        contactBtn.setHorizontalAlignment(SwingConstants.CENTER);
        contactBtn.setVerticalAlignment(SwingConstants.CENTER);
        Dealer.setTripleSizes(contactBtn, Dealer.s(40), Dealer.s(40));
        contactBtn.setRolloverIcon(new ImageIcon("Client/res/images/icons/fillContactRollover.png"));
        contactBtn.setSelectedIcon(new ImageIcon("Client/res/images/icons/fillContactSelected.png"));
        Dealer.setButtonTransparent(contactBtn);

        contactBtn.addActionListener(e -> {
            cardLayoutTc.show(scrollPanel, "contactPanel");
        });

        notificationBtn = new JRadioButton(new ImageIcon("Client/res/images/icons/notificationIdle.png"),
                true);
        notificationBtn.setHorizontalAlignment(SwingConstants.CENTER);
        notificationBtn.setVerticalAlignment(SwingConstants.CENTER);
        Dealer.setTripleSizes(notificationBtn, Dealer.s(40), Dealer.s(40));
        notificationBtn.setRolloverIcon(new ImageIcon("Client/res/images/icons/notificationRollover.png"));
        notificationBtn.setSelectedIcon(new ImageIcon("Client/res/images/icons/notificationSelected.png"));
        Dealer.setButtonTransparent(notificationBtn);

        notificationBtn.addActionListener(e -> {
            cardLayoutTc.show(scrollPanel, "notificationPanel");
            crCardLayout.show(chatRoomPanel, "00000Cp");
        });


        mailBtn = new JButton(new ImageIcon("Client/res/images/icons/letterIdle.png"));
        mailBtn.setHorizontalAlignment(SwingConstants.CENTER);
        mailBtn.setVerticalAlignment(SwingConstants.CENTER);
        Dealer.setTripleSizes(mailBtn, Dealer.s(40), Dealer.s(40));
        mailBtn.setRolloverIcon(new ImageIcon("Client/res/images/icons/letterRollover.png"));
        mailBtn.setPressedIcon(new ImageIcon("Client/res/images/icons/letterRollover.png"));
        Dealer.setButtonTransparent(mailBtn);

        mailBtn.addActionListener(e -> {}); // 使用openWebPage()来打开指定网页


        folderBtn = new JButton(new ImageIcon("Client/res/images/icons/folderIdle.png"));
        folderBtn.setHorizontalAlignment(SwingConstants.CENTER);
        folderBtn.setVerticalAlignment(SwingConstants.CENTER);
        Dealer.setTripleSizes(folderBtn, Dealer.s(40), Dealer.s(40));
        folderBtn.setRolloverIcon(new ImageIcon("Client/res/images/icons/folderRollover.png"));
        folderBtn.setPressedIcon(new ImageIcon("Client/res/images/icons/folderRollover.png"));
        Dealer.setButtonTransparent(folderBtn);


        mainMenuBtnGp = new ButtonGroup();
        mainMenuBtnGp.add(msgBtn);
        mainMenuBtnGp.add(contactBtn);
        mainMenuBtnGp.add(notificationBtn);

        menuPanel.add(avatarLabel);

        menuPanel.add(Box.createVerticalStrut(Dealer.s(30)));
        menuPanel.add(msgBtn);
        menuPanel.add(contactBtn);
        menuPanel.add(notificationBtn);

        menuPanel.add(Box.createVerticalStrut(Dealer.s(90)));

        menuPanel.add(mailBtn);
        menuPanel.add(folderBtn);

        // --touchCardPanel

        tcHeadBar = new JPanel();
        tcHeadBar.setPreferredSize(new Dimension(Dealer.s(140), Dealer.s(40)));
        tcHeadBar.setMaximumSize(new Dimension(Dealer.s(300), Dealer.s(40)));
        tcHeadBar.setMinimumSize(new Dimension(Dealer.s(140), Dealer.s(40)));

        scrollPanel = new JPanel();
        scrollPanel.setPreferredSize(new Dimension(Dealer.s(140), Dealer.s(360)));
        scrollPanel.setMaximumSize(new Dimension(Dealer.s(300), Integer.MAX_VALUE));
        scrollPanel.setMinimumSize(new Dimension(Dealer.s(140), Dealer.s(0)));

        touchCardPanel.add(tcHeadBar);
        touchCardPanel.add(scrollPanel);

        cardLayoutTc = new CardLayout();
        scrollPanel.setLayout(cardLayoutTc);

        msgPanel = new JPanel(new BorderLayout());
        msgPanel.setOpaque(false);
        scrollPanel.add(msgPanel, "msgPanel");

        contactPanel = new JPanel(new BorderLayout());
        contactPanel.setOpaque(false);
        scrollPanel.add(contactPanel, "contactPanel");

        notificationPanel = new JPanel(new BorderLayout());
        notificationPanel.setOpaque(false);
        scrollPanel.add(notificationPanel, "notificationPanel");

        // --搜索栏 和 添加好友
        tcHeadBar.setLayout(null);

        JTextField searchField = new JTextField();
        searchField.setBounds(10, 10, 100, 20);
        searchField.setFont(textEditedStyleFont);
        searchField.addFocusListener(new HintFocusListener(searchField, "°、点此搜索"));
        tcHeadBar.add(searchField);

        JButton addBtn = new JButton("+");
        addBtn.setBounds(115, 10, 20, 20);
        addBtn.setFont(new Font("黑体", Font.BOLD, 11));
        addBtn.addActionListener(e -> makeFriendDialog.setVisible(true));
        tcHeadBar.add(addBtn);

        // --sp 的滚动面板

        msgInfoList = new InfoList();

        JScrollPane msgSp = new JScrollPane(msgInfoList);
        JScrollBar verticalScrollBarM = msgSp.getVerticalScrollBar();
        verticalScrollBarM.setUnitIncrement(Dealer.s(20));
        msgPanel.add(msgSp, BorderLayout.CENTER);

        InfoList contactInfoList = new InfoList();

        JScrollPane contactSp = new JScrollPane(contactInfoList);
        JScrollBar verticalScrollBarC = contactSp.getVerticalScrollBar();
        verticalScrollBarC.setUnitIncrement(Dealer.s(20));
        contactPanel.add(contactSp, BorderLayout.CENTER);

        InfoList notificationInfoList = new InfoList();

        JScrollPane notificationSp = new JScrollPane(notificationInfoList);
        JScrollBar verticalScrollBarN = notificationSp.getVerticalScrollBar();
        verticalScrollBarN.setUnitIncrement(Dealer.s(20));
        notificationPanel.add(notificationSp, BorderLayout.CENTER);


        // --
        // TODO chatRoomPanel:
        /* crCardLayout - [customized]"crCardPanel<uid/gid>"(s)
                        -       |-headlinePanel  - titleLabel
                        -       |-            |- statusLabel
                        -       |-            |- uidLabel
                        -       |-chatMsgPanel - cmScrollPane - (db)MessageBlock(s)
                        -       |-sendMsgPanel - smScrollPane - messageArea
                        -       |-            |- sendBtn
         */
        // TODO 为每个infoItem绑定一个事件，

        // --notificationPanel对应的唯一一个聊天室——公共聊天室（包括系统消息也在这里）
        CrCardPanel notificationCp = new CrCardPanel(CrCardPanel.TYPE_GROUP);
        notificationCp.setTitle("公共聊天室");
        notificationCp.refreshTitleText();
        notificationCp.setStatusAndRefresh(CrCardPanel.ONLINE);
        notificationCp.setIdAndRefresh("00000");

        addCrCardPanelToCardLayout(chatRoomPanel, "00000Cp", notificationCp);

        // ===

        setMinimumSize(new Dimension(350, 200));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));
        setSize(620, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(false);
    }

    public MakeFriendDialog getMakeFriendDialog() {
        return makeFriendDialog;
    }

    // 添加组件时记录
    public void addCrCardPanelToCardLayout(Container parent, String name, CrCardPanel comp) {
        parent.add(comp, name);
        crCardPanelMap.put(name, comp);
    }

    // 查找组件
    public CrCardPanel getCrCardPanelByName(String name) {
        return crCardPanelMap.get(name);
    }

    public static Font getTextEditedStyleFont() {
        return textEditedStyleFont;
    }

    public static Font getNotificationFont() {
        return notificationFont;
    }

    public BufferedImage getBf() {
        return bf;
    }

    /**
     * 更新头像
     */
    public void updateAvatar() {
        bf = CopeImageUtil.cutHeadImages("Client/res/images/own/" + CMsgProcessor.getChatClient().getUid()
                + ".png", Dealer.s(34));
        if (bf != null) {
            avatarLabel.setIcon(new ImageIcon(bf));
        }
    }

    /**
     * 为每个好友和群组创建一个item和选项卡
     */
    public void addFriendsAndGroupsToItemsThenNewCp() {
        ArrayList<UserItem> friends = CMsgProcessor.getChatClient().getFriends();
        ArrayList<GroupItem> groupItems = CMsgProcessor.getChatClient().getGroups();
        for (UserItem u : friends) {
            System.out.println("adding " + u.uid + "," + u.nickname + " to cps");
            BufferedImage bufferedImage = CopeImageUtil.cutHeadImages("Client/res/images/relation/" +
                    "userAvatars/" + u.uid + ".png", Dealer.s(34));
            ImageIcon avatar = new ImageIcon(bufferedImage);
            InfoItem infoItem = new InfoItem(u.nickname, "UID:" + u.uid, avatar,
                    Dealer.s(40));
            CrCardPanel uCp = new CrCardPanel(CrCardPanel.TYPE_PERSON);
            uCp.setTitle(u.nickname);
            uCp.refreshTitleText();
            uCp.setStatusAndRefresh(CrCardPanel.OFFLINE);
            uCp.setIdAndRefresh(u.uid);
            addCrCardPanelToCardLayout(chatRoomPanel, u.uid + "Cp", uCp);
            infoItem.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    crCardLayout.show(chatRoomPanel, u.uid + "Cp");
                    uCp.resizePartsToFit();
                    uCp.revalidate();
                }
            });
            msgInfoList.addItem(infoItem);
//            uCp.resizePartsToFit();
        }
        for (GroupItem g : groupItems) {
            BufferedImage bufferedImage = CopeImageUtil.cutHeadImages("Client/res/images/relation/" +
                    "groupAvatars/" + g.gid + ".png", Dealer.s(34));
            ImageIcon avatar = new ImageIcon(bufferedImage);
            InfoItem infoItem = new InfoItem(g.groupName, "GID:" + g.gid, avatar,
                    Dealer.s(40));
            CrCardPanel gCp = new CrCardPanel(CrCardPanel.TYPE_GROUP);
            gCp.setTitle(g.groupName);
            gCp.refreshTitleText();
            gCp.setIdAndRefresh(g.gid);
            addCrCardPanelToCardLayout(chatRoomPanel, g.gid + "Cp", gCp);
            infoItem.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    crCardLayout.show(chatRoomPanel, g.gid + "Cp");
                    gCp.resizePartsToFit();
                    gCp.revalidate();
                }
            });
            msgInfoList.addItem(infoItem);
//            gCp.resizePartsToFit();
        }
        chatRoomPanel.revalidate();
    }

    /**
     * 添加某个好友至itemList，并为其创建一个选项卡
     */
    public void addFriendToItemListAndNewCp(UserItem friend) {
        System.out.println("adding " + friend.uid + "," + friend.nickname + "to cps");
        BufferedImage bufferedImage = CopeImageUtil.cutHeadImages("Client/res/images/relation/userAvatars/"
                + friend.uid + ".png", Dealer.s(34));
        ImageIcon avatar = new ImageIcon(bufferedImage);
        InfoItem infoItem = new InfoItem(friend.nickname, "UID:" + friend.uid, avatar,
                Dealer.s(40));
        CrCardPanel uCp = new CrCardPanel(CrCardPanel.TYPE_PERSON);
        uCp.setTitle(friend.nickname);
        uCp.refreshTitleText();
        uCp.setStatusAndRefresh(CrCardPanel.OFFLINE);
        uCp.setIdAndRefresh(friend.uid);
        addCrCardPanelToCardLayout(chatRoomPanel, friend.uid + "Cp", uCp);
        infoItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                crCardLayout.show(chatRoomPanel, friend.uid + "Cp");
                uCp.resizePartsToFit();
                uCp.revalidate();
            }
        });
        msgInfoList.addItem(infoItem);
//        uCp.resizePartsToFit();
        chatRoomPanel.revalidate();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Dealer.adaptToOS();
            ClientMainFrame clientMainFrame = new ClientMainFrame("RealChat Test");
            Dealer.adjustScreen(clientMainFrame);
            clientMainFrame.setLocationRelativeTo(null);
            clientMainFrame.setVisible(true);
        });
    }

}
