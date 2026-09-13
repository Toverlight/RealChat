package ClientSide.customized.rePanel.bubble;

import ClientSide.customized.Dealer;

import javax.swing.*;
import java.awt.*;

public class MessageBlock extends JPanel {
    private JLabel avatarLabel;       // 头像
    private JLabel nicknameLabel;     // 昵称
    private JLabel timeLabel;         // 时间
    private MessageBubble bubble;     // 消息气泡

    public MessageBlock(ImageIcon avatar, String nickname, String time, MessageBubble bubble) {
        this.bubble = bubble;
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.setOpaque(false);

        JPanel emptyPanel = new JPanel();
        JPanel contentPanel = new JPanel();

        contentPanel.setLayout(null);

        boolean fromSelf = bubble.isFromSelf();

        if (fromSelf) {
            add(emptyPanel);
            add(contentPanel);
        } else {
            add(contentPanel);
            add(emptyPanel);
        }


        // 创建头像
        int avatarSideLen = avatar.getIconWidth();

        avatarLabel = new JLabel(avatar);

        // 创建昵称标签
        nicknameLabel = new JLabel(nickname);
        nicknameLabel.setFont(new Font("黑体", Font.PLAIN, Dealer.s(10)));
        nicknameLabel.setForeground(Color.BLACK);
        nicknameLabel.setOpaque(false);


        // 创建时间标签
        timeLabel = new JLabel(time);
        timeLabel.setFont(new Font("Consolas", Font.PLAIN, Dealer.s(10)));
        timeLabel.setForeground(Color.GRAY);
        timeLabel.setOpaque(false);


        int bubbleWidth = bubble.getPreferredSize().width;
        int bubbleHeight = bubble.getPreferredSize().height;

        // 设置气泡的位置

        int bubbleY = Dealer.s(35);

        this.add(bubble);

        // 自动调整消息块的大小
        this.setPreferredSize(new Dimension(400, bubbleHeight + Dealer.s(55)));
        this.setMinimumSize(new Dimension(0, bubbleHeight + Dealer.s(55)));
        this.setMaximumSize(new Dimension(Integer.MAX_VALUE, bubbleHeight + Dealer.s(55)));


        contentPanel.add(avatarLabel);
        contentPanel.add(nicknameLabel);
        contentPanel.add(timeLabel);
        contentPanel.add(bubble);

        Dealer.setTripleSizes(contentPanel, bubbleWidth + Dealer.s(100),
                bubbleHeight + Dealer.s(55));


        if (fromSelf) {
            avatarLabel.setBounds(contentPanel.getPreferredSize().width - avatarSideLen - Dealer.s(10),
                    Dealer.s(15), avatarSideLen, avatarSideLen);
            nicknameLabel.setBounds(contentPanel.getPreferredSize().width - avatarSideLen - Dealer.s(40),
                    0, Dealer.s(100), Dealer.s(12));
            timeLabel.setBounds(contentPanel.getPreferredSize().width - avatarSideLen - Dealer.s(50),
                    Dealer.s(20), 100, 20);
        } else {
            avatarLabel.setBounds(Dealer.s(10), Dealer.s(15), avatarSideLen, avatarSideLen);
            nicknameLabel.setBounds(Dealer.s(40), 0, Dealer.s(100), Dealer.s(12));
            timeLabel.setBounds(Dealer.s(50), Dealer.s(20), 100, 20);
        }
        int bubbleX = bubble.isFromSelf()
                ? contentPanel.getPreferredSize().width - bubbleWidth - Dealer.s(50)  // 自己发的消息靠右
                : avatarLabel.getWidth() + Dealer.s(10);                      // 对方发的消息靠左
        bubble.setBounds(bubbleX, bubbleY, bubbleWidth, bubbleHeight);


    }
}

