package ClientSide.customized.rePanel.infoItem;

import ClientSide.customized.Dealer;
import ClientSide.customized.reWindow.NotificationPopup;
import ClientSide.reference.CopeImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class FriendListTest {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Friend List Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        InfoList infoList = new InfoList();

        JScrollPane scrollPane = new JScrollPane(infoList);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        // 设置滚动灵敏度
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setUnitIncrement(Dealer.s(20)); // 设置滚动步长，数字越大滚动越快

        BufferedImage bf = CopeImageUtil.cutHeadImages("Client/res/images/icons/xixi.png", Dealer.s(34));

        for (int i = 1; i <= 20; i++) {
            ImageIcon avatar = new ImageIcon(bf); // 替换为实际头像路径
            InfoItem infoItem = new InfoItem("Friend " + i, "Last message from Friend " + i, avatar,
                    Dealer.s(40));
            int finalI = i;
            infoItem.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    NotificationPopup.showNotification("嘻嘻(●'◡'●)" + finalI, new Font("黑体",
                            Font.PLAIN, 25));
                }
            });
            infoList.addItem(infoItem);
        }

        frame.add(scrollPane);
        frame.setVisible(true);

        Dealer.adjustScreen(frame);
    }
}

