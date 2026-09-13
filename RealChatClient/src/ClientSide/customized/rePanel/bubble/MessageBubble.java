package ClientSide.customized.rePanel.bubble;

import ClientSide.customized.Dealer;

import javax.swing.*;
import java.awt.*;

public class MessageBubble extends JPanel {
    private boolean fromSelf; // 判断是否自己发送
    private String message;   // 消息内容
    private int maxWidth;     // 最大宽度
    private Font font;        // 字体

    public MessageBubble(boolean fromSelf, String message, int maxWidth, Font font) {
        this.fromSelf = fromSelf;
        this.message = message;
        this.maxWidth = maxWidth;
        this.font = font;
        this.setOpaque(false); // 背景透明
    }

    public boolean isFromSelf() {
        return fromSelf;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 计算背景颜色和字体颜色
        Color bgColor = fromSelf ? new Color(0, 120, 215) : Color.WHITE;
        Color fontColor = fromSelf ? Color.WHITE : Color.BLACK;

        // 绘制消息气泡
        int padding = Dealer.s(10); // 内边距
        int arc = Dealer.s(15);     // 圆角大小
        int width = getPreferredSize().width;
        int height = getPreferredSize().height;

        // 气泡位置调整
        int x = fromSelf ? getWidth() - width : 0;
        g2d.setColor(bgColor);
        g2d.fillRoundRect(x, 0, width, height, arc, arc);


        // 绘制文字
        g2d.setColor(fontColor);
        g2d.setFont(font);
        drawStringMultiLine(g2d, message, x + padding, font.getSize() + padding, maxWidth - padding);

    }

    @Override
    public Dimension getPreferredSize() {
        // 计算消息气泡的宽高
        FontMetrics metrics = getFontMetrics(font);
        int lineHeight = metrics.getHeight();
        int lineWidth = 0;
        int lineCount = 1;

        String[] lines = message.split("\n");
        for (String line : lines) {
            int width = metrics.stringWidth(line);
            if (width > maxWidth) {
                lineWidth = maxWidth;
                lineCount += (int) Math.ceil((double) width / maxWidth);
            } else {
                lineWidth = Math.max(lineWidth, width);
            }
        }

        return new Dimension(lineWidth + Dealer.s(10),
                lineHeight * lineCount + Dealer.s(15)); // 添加适当边距
    }

    private void drawStringMultiLine(Graphics g, String text, int x, int y, int maxWidth) {
        FontMetrics metrics = g.getFontMetrics();
        int lineHeight = metrics.getHeight();

        String[] lines = text.split("\n");
        for (String line : lines) {
            while (line.length() > 0) {
                int charsToDraw = 0;
                int width = 0;

                for (int i = 0; i < line.length(); i++) {
                    width = metrics.stringWidth(line.substring(0, i + 1));
                    if (width > maxWidth) break;
                    charsToDraw = i + 1;
                }

                g.drawString(line.substring(0, charsToDraw), x, y);
                line = line.substring(charsToDraw);
                y += lineHeight;
            }
        }
    }

}
