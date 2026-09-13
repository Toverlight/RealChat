package ClientSide;

import ClientSide.customized.CMsgProcessor;
import ClientSide.customized.Dealer;
import ClientSide.customized.checker.GIDChecker;
import ClientSide.customized.checker.UIDChecker;
import ClientSide.customized.enums.protocol.RequestFlag;
import ClientSide.customized.enums.protocol.SearchFlag;
import ClientSide.customized.enums.protocol.SearchUsage;
import ClientSide.customized.reListener.HintFocusListener;
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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.List;

public class MakeFriendDialog extends JDialog {
    private JScrollPane resultSp;
    private InfoList resultList;
    public MakeFriendDialog(ClientMainFrame clientMainFrame, String title) {
        super(clientMainFrame, title, true);
        setTitle(title);

        Container container = getContentPane();
        container.setLayout(null);

        JTextField searchForFriendsOrGroups = new JTextField();
        searchForFriendsOrGroups.setFont(ClientMainFrame.getTextEditedStyleFont());
        searchForFriendsOrGroups.setBounds(Dealer.s(10), Dealer.s(10), Dealer.s(200), Dealer.s(20));
        searchForFriendsOrGroups.addFocusListener(new HintFocusListener(searchForFriendsOrGroups,
                "搜索好友uid或群组gid"));
        container.add(searchForFriendsOrGroups);

        JButton searchBtn = new JButton("搜索");
        searchBtn.setFont(ClientMainFrame.getNotificationFont());
        searchBtn.setBounds(Dealer.s(250), Dealer.s(10), Dealer.s(40), Dealer.s(20));
        searchBtn.addActionListener(e -> {
            String id = searchForFriendsOrGroups.getText();
            if (new UIDChecker().checkValid(id)) {
                if (CMsgProcessor.getChatClient().checkIfUserInFriends(id))
                    SwingUtilities.invokeLater(() ->
                            NotificationPopup.showNotification("该uid对应用户已经是你的好友",
                                    ClientMainFrame.getNotificationFont()));
                else if (id.equals(CMsgProcessor.getChatClient().getUid()))
                    SwingUtilities.invokeLater(() ->
                            NotificationPopup.showNotification("不能添加自己唷",
                                    ClientMainFrame.getNotificationFont()));
                else
                    CMsgProcessor.search(SearchFlag.USER, id, SearchUsage.MAKE_FRIEND_OR_JOIN_GROUP);
            } else if (new GIDChecker().checkValid(id)) {
                if (CMsgProcessor.getChatClient().checkIfGroupInGroups(id))
                    SwingUtilities.invokeLater(() ->
                            NotificationPopup.showNotification("你已在这个群组",
                                    ClientMainFrame.getNotificationFont()));
                else
                    CMsgProcessor.search(SearchFlag.GROUP, id, SearchUsage.MAKE_FRIEND_OR_JOIN_GROUP);
            } else {
                SwingUtilities.invokeLater(() ->
                        NotificationPopup.showNotification("请输入6位uid或5位gid进行搜索！",
                                ClientMainFrame.getNotificationFont()));

            }
        });
        container.add(searchBtn);

        resultList = new InfoList();

        resultSp = new JScrollPane(resultList);
        resultSp.setBounds(Dealer.s(10), Dealer.s(60), Dealer.s(280), Dealer.s(80));
        resultSp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        resultSp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        container.add(resultSp);


        // 添加窗口监听器，在窗口关闭时隐藏窗口
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setVisible(false);  // 隐藏窗口，而不是关闭
            }
        });

        setLocationRelativeTo(clientMainFrame);
        setSize(Dealer.s(300), Dealer.s(200));
        setResizable(false);
        setVisible(false);

    }

    /**
     * 批量添加用户或群组到搜索结果容器。若不用某一者，对应参数传 null。
     */
    public void batchAddUsersAndGroups(List<UserItem> userItems, List<GroupItem> groupItems) {
        Dealer.clearPanel(resultList);
        if (userItems != null) {
            for (UserItem u : userItems) {
                BufferedImage bf = CopeImageUtil.cutHeadImages("Client/res/images/relation/userAvatars/"
                        + u.uid + ".png", Dealer.s(34));
                ImageIcon imageIcon = new ImageIcon(bf);
                InfoItem infoItem = new InfoItem(u.nickname, "UID:" + u.uid,
                        imageIcon, Dealer.s(40));
                infoItem.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        int option = JOptionPane.showConfirmDialog(
                            MakeFriendDialog.this,
                            "你确定要发出好友申请吗？",
                                "好友申请",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE
                        );
                        if (option == JOptionPane.YES_OPTION) {
                            System.out.println("发送好友申请");
                            // TODO 发送好友申请
                            CMsgProcessor.request(RequestFlag.USER, u.uid);
                            NotificationPopup.showNotification("成功发送好友申请，等待对方处理",
                                    ClientMainFrame.getNotificationFont());
                        } else if (option == JOptionPane.NO_OPTION) {
                            System.out.println("取消好友申请发送");
                            NotificationPopup.showNotification("取消好友申请发送",
                                    ClientMainFrame.getNotificationFont());
                        }
                    }
                });
                resultList.addItem(infoItem);
                System.out.println("添加search infoItem");
            }
        }
        if (groupItems != null) {
            for (GroupItem g : groupItems) {
                // TODO 群组加入
            }
        }

    }
}
