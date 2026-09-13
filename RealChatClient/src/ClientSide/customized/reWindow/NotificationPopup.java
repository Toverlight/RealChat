package ClientSide.customized.reWindow;

import ClientSide.customized.Dealer;

import javax.swing.*;
import java.awt.*;

public class NotificationPopup {

    public static void showNotification(String message, Font font) {
        // 创建JWindow
        JWindow window = new JWindow();
        window.setSize(message.length() * font.getSize() + Dealer.s(10),
                font.getSize() + Dealer.s(10));
        window.setLayout(new BorderLayout());
        window.setLocationRelativeTo(null); // 居中显示

        // 创建圆角矩形面板
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // 绘制圆角矩形背景
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(255, 255, 255)); // 设置背景色
                g2d.fillRoundRect(0, 0, window.getWidth(),
                        window.getHeight(),
                        Dealer.s(15), Dealer.s(15)); // 绘制圆角矩形
            }
        };
        panel.setOpaque(false); // 使得面板透明
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel messageLabel = new JLabel(message);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setVerticalAlignment(SwingConstants.CENTER);
        messageLabel.setForeground(Color.BLACK);
        messageLabel.setFont(font);
        panel.add(messageLabel);

        window.getContentPane().add(panel, BorderLayout.CENTER);

        // 淡出效果
        Timer fadeOutTimer = new Timer(1000, null); // 延迟1秒
        fadeOutTimer.setRepeats(false); // 只执行一次
        fadeOutTimer.addActionListener(e -> {
            Timer fadeOutEffect = new Timer(20, e1 -> {
                float opacity = window.getOpacity();
                if (opacity > 0.02f) {
                    window.setOpacity(opacity - 0.02f); // 每次减少不透明度
                } else {
                    ((Timer) e1.getSource()).stop(); // 停止淡出定时器
                    window.dispose(); // 关闭窗口
                }
            });
            fadeOutEffect.start(); // 启动淡出效果
        });
        fadeOutTimer.start();

        // 防止窗口捕获鼠标事件
        window.setFocusableWindowState(false);
        window.setOpacity(1f);
        window.setBackground(new Color(255, 255, 255, 0)); // 背景透明
        window.setAlwaysOnTop(true);
        window.setVisible(true);

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            showNotification("登录成功", new Font("黑体", Font.PLAIN, Dealer.s(10)));
        });
    }
}

