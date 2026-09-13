package ClientSide.customized.rePanel;

import ClientSide.ClientMainFrame;
import ClientSide.customized.CMsgProcessor;
import ClientSide.customized.Dealer;
import ClientSide.customized.rePanel.bubble.MessageBlock;
import ClientSide.customized.reWindow.NotificationPopup;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;


/**
 * 聊天室卡片页容器
 */
public class CrCardPanel extends JPanel {

    public static final int OFFLINE = 0;
    public static final int ONLINE = 1;

    public static final int TYPE_PERSON = 0;    // 个人
    public static final int TYPE_GROUP = 1;     // 群组

    public static final ImageIcon onlineIcon = new ImageIcon(
            new ImageIcon("Client/res/images/icons/online.png").getImage().
            getScaledInstance(Dealer.s(10), Dealer.s(10), Image.SCALE_DEFAULT));
    public static final ImageIcon offlineIcon = new ImageIcon(
            new ImageIcon("Client/res/images/icons/offline.png").getImage().
            getScaledInstance(Dealer.s(10), Dealer.s(10), Image.SCALE_DEFAULT));;

    private int type;               // 个人类型还是群组类型

    private String id;              // 对方id

    private String title;
    private int peopleNum = 0;      // 群组时有效，显示群聊人数

    private JPanel headlinePanel;
    private JPanel chatMsgPanel;
    private JPanel sendMsgPanel;

    private JLabel titleLabel;
    private JLabel statusLabel;
    private JLabel idLabel; // 显示 uid（用户，6位） 或 gid（群组，5位）

    private JScrollPane cmScrollPane;
    private JPanel messageBlockPanel;
    // messageBlock 临时添加

    private JPanel smSpPanel;
    private JPanel sendBtnPanel;

    private JScrollPane smScrollPane;
    private JTextArea messageArea;
    private JButton sendBtn;

    public CrCardPanel(int type) {
        this.type = type;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        headlinePanel = new JPanel();
        chatMsgPanel = new JPanel();
        sendMsgPanel = new JPanel();

        add(headlinePanel);
        add(chatMsgPanel);
        add(sendMsgPanel);

        headlinePanel.setBackground(new Color(240, 221, 227));
        headlinePanel.setLayout(null);
        chatMsgPanel.setBackground(new Color(242, 242, 242));
        sendMsgPanel.setBackground(new Color(232, 242, 242));

        headlinePanel.setPreferredSize(new Dimension(Dealer.s(320), Dealer.s(50)));
        headlinePanel.setMinimumSize(new Dimension(0, Dealer.s(50)));
        headlinePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Dealer.s(50)));

        titleLabel = new JLabel();
        statusLabel = new JLabel();
        idLabel = new JLabel();

        headlinePanel.add(titleLabel);
        headlinePanel.add(statusLabel);
        headlinePanel.add(idLabel);

        messageBlockPanel = new JPanel();
        messageBlockPanel.setLayout(new BoxLayout(messageBlockPanel, BoxLayout.Y_AXIS));
        cmScrollPane = new JScrollPane(messageBlockPanel);
        cmScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        cmScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JScrollBar verticalScrollBar = cmScrollPane.getVerticalScrollBar();
        verticalScrollBar.setUnitIncrement(Dealer.s(20));

        chatMsgPanel.setLayout(new BorderLayout());
        chatMsgPanel.add(cmScrollPane, BorderLayout.CENTER);

        smSpPanel = new JPanel();
        smSpPanel.setBackground(new Color(215, 219, 236));
        sendBtnPanel = new JPanel();
        sendBtnPanel.setBackground(new Color(215, 219, 236));

        sendMsgPanel.setLayout(new BoxLayout(sendMsgPanel, BoxLayout.X_AXIS));

        sendMsgPanel.add(smSpPanel);
        sendMsgPanel.add(sendBtnPanel);

        sendMsgPanel.setPreferredSize(new Dimension(Dealer.s(320), Dealer.s(100)));
        sendMsgPanel.setMinimumSize(new Dimension(0, Dealer.s(100)));
        sendMsgPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Dealer.s(100)));



        // details

        titleLabel.setBounds(140, 5, 150, 16);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 12));

        statusLabel.setBounds(140, 30, 100, 16);
        statusLabel.setHorizontalTextPosition(SwingConstants.LEFT);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setIcon(offlineIcon);
        statusLabel.setText("离线");
        statusLabel.setFont(new Font("微软雅黑", Font.BOLD, 10));

        idLabel.setFont(new Font("黑体", Font.PLAIN, 10));
        idLabel.setBounds(10, 30, 150, 16);

        messageArea = new JTextArea();
        messageArea.setFont(new Font("黑体", Font.PLAIN, Dealer.s(10)));
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        smScrollPane = new JScrollPane(messageArea);
        sendBtn = new JButton("发送");
        sendBtn.setFont(new Font("黑体", Font.PLAIN, 12));
        sendBtn.addActionListener(e -> {
            sendChatMsg();
        });

        smSpPanel.add(smScrollPane);

        smSpPanel.setPreferredSize(new Dimension(Dealer.s(260), Dealer.s(150)));
        smSpPanel.setMinimumSize(new Dimension(Dealer.s(0), Dealer.s(150)));
        smSpPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Dealer.s(150)));

        smSpPanel.setLayout(new BorderLayout());
        smSpPanel.setBorder(new EmptyBorder(Dealer.s(10), Dealer.s(15), Dealer.s(10), Dealer.s(10)));
        smSpPanel.add(smScrollPane, BorderLayout.CENTER);


        sendBtnPanel.add(sendBtn);

        sendBtnPanel.setPreferredSize(new Dimension(Dealer.s(60), Dealer.s(150)));
        sendBtnPanel.setMinimumSize(new Dimension(Dealer.s(50), Dealer.s(150)));
        sendBtnPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Dealer.s(150)));

        sendBtnPanel.setLayout(new BorderLayout());
        sendBtnPanel.setBorder(new EmptyBorder(Dealer.s(20), Dealer.s(30), Dealer.s(50), Dealer.s(20)));
        sendBtnPanel.add(sendBtn, BorderLayout.CENTER);

        if (type == TYPE_GROUP) {
            statusLabel.setVisible(false);
            idLabel.setVisible(false);
        }

    }

    /**
     * 发送聊天消息给对方
     */
    public void sendChatMsg() {
        if (!"".equals(messageArea.getText())) {
            if ("在线".equals(statusLabel.getText()))
                CMsgProcessor.chat(type, id, messageArea.getText());
            else
                SwingUtilities.invokeLater(() ->
                        NotificationPopup.showNotification("只能与在线用户对话",
                                ClientMainFrame.getNotificationFont()));

        }
        messageArea.setText("");
    }

    /**
     * 设置顶栏标题文本，设置完记得刷新
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 设置群聊人数为
     * @param peopleNum 群聊人数
     */
    public void setPeopleNum(int peopleNum) {
        this.peopleNum = peopleNum;
    }

    /**
     * 刷新标题文本
     */
    public void refreshTitleText() {
        if (type == TYPE_PERSON) {
            titleLabel.setText(title);
        } else if (type == TYPE_GROUP) {
            titleLabel.setText(title + " ( " + peopleNum + " )");
        }
    }


    /**
     * 设置状态并刷新显示
     */
    public void setStatusAndRefresh(int status) {
        if (status == ONLINE) {
            statusLabel.setText("在线");
            statusLabel.setIcon(onlineIcon);
        } else if (status == OFFLINE) {
            statusLabel.setText("离线");
            statusLabel.setIcon(offlineIcon);
        }
    }

    /**
     * 设置id并刷新显示
     * @param id UID(6位)或GID(5位)
     */
    public void setIdAndRefresh(String id) {
        this.id = id;
        if (type == TYPE_PERSON) {
            idLabel.setText("UID:" + id);
        } else if (type == TYPE_GROUP) {
            idLabel.setText("GID:" + id);
        }
    }



    public void addMessageBlock(MessageBlock messageBlock) {
        messageBlockPanel.add(messageBlock);
    }

    /**
     * 将滑块滑至最底部
     */
    public void scrollToBottom() {
        // 滚动到内容面板的最底部
        Rectangle rect = new Rectangle(0, messageBlockPanel.getHeight() - 1, 1, 1);
        messageBlockPanel.scrollRectToVisible(rect);
        JScrollBar verticalBar = cmScrollPane.getVerticalScrollBar();
        verticalBar.setValue(verticalBar.getMaximum());
    }

    /**
     * 解决后来加入主窗口线程的CrCardPanel中部分组件大小、位置及字体不适合屏幕分辨率的问题
     */
    public void resizePartsToFit() {
        double scale = 1.0;
        if (titleLabel.getWidth() < Dealer.s(150)) {
            scale = Dealer.getScale();
        } else if (titleLabel.getWidth() > Dealer.s(150)){
            scale = 1.0 / Dealer.getScale();
        }
        Dealer.resizeComponents(headlinePanel, scale);
        Dealer.resizeComponents(sendMsgPanel, scale);
    }
}
