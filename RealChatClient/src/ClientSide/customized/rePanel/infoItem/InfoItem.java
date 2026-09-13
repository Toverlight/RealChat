package ClientSide.customized.rePanel.infoItem;

import ClientSide.customized.Dealer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 头像直径应设为 Dealer.s(infoItem 的高度减6)。
 * infoItem高度设为 Dealer.s(40)
 */
public class InfoItem extends JPanel{
    private final String name;
    private final String message;
    private final ImageIcon avatar;
    private boolean isSelected = false;
    private boolean isHovered = false;
    private final int height;

    public InfoItem(String name, String message, ImageIcon avatar, int height) {
        this.name = name;
        this.message = message;
        this.avatar = avatar;
        this.height = height;

        setPreferredSize(new Dimension(0, height));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                InfoList parent = (InfoList) getParent();
                parent.setSelectedItem(InfoItem.this);
            }
        });
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 背景颜色
        if (isSelected) {
            g2d.setColor(new Color(100, 149, 237)); // 选中颜色
        } else if (isHovered) {
            g2d.setColor(new Color(230, 230, 230)); // 悬停颜色
        } else {
            g2d.setColor(Color.WHITE); // 默认颜色
        }
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), Dealer.s(10), Dealer.s(10));

        // 绘制头像
        if (avatar != null) {
            g2d.drawImage(avatar.getImage(), Dealer.s(3), Dealer.s(3), null);
        }

        // 绘制名称
        g2d.setFont(new Font("黑体", Font.PLAIN, Dealer.s(10)));
        if (isSelected) {
            g2d.setColor(Color.WHITE);
        } else {
            g2d.setColor(Color.BLACK);
        }
        g2d.drawString(name, height, Dealer.s(16));

        // 绘制消息
        g2d.setFont(new Font("黑体", Font.PLAIN, Dealer.s(8)));
        if (isSelected) {
            g2d.setColor(Color.WHITE);
        } else {
            g2d.setColor(Color.GRAY);
        }
        g2d.drawString(message, height, Dealer.s(28));
    }
}

