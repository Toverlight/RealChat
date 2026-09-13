package ClientSide.customized.reLabel;

import javax.swing.*;
import java.awt.*;

/**
 * 带阴影效果的文字标签
 */
public class ShadowLabel extends JLabel {
    private Color shadowColor;               // 阴影颜色
    private int shadowOffsetX = 2;           // 阴影水平偏移
    private int shadowOffsetY = 2;           // 阴影垂直偏移
    private float shadowAlpha = 0.5f;        // 阴影透明度

    public ShadowLabel(String text) {
        super(text);
        shadowColor = new Color(50, 53, 58);
    }

    public void setShadowColor(Color shadowColor) {
        this.shadowColor = shadowColor;
        repaint();
    }

    public void setShadowOffsetX(int offsetX) {
        this.shadowOffsetX = offsetX;
        repaint();
    }

    public void setShadowOffsetY(int offsetY) {
        this.shadowOffsetY = offsetY;
        repaint();
    }

    public void setShadowAlpha(float alpha) {
        this.shadowAlpha = Math.max(0, Math.min(1, alpha)); // 限制透明度在 0~1
        repaint();
    }

    /**
     * 设置一般文本（黑色）
     * @param text 文本
     */
    public void setInfoText(String text) {
        setForeground(Color.BLACK);
        setText(text);
    }

    /**
     * 设置错误文本（红色）
     * @param text 文本
     */
    public void setErrText(String text) {
        setForeground(Color.RED);
        setText(text);
    }

    /**
     * 设置通过文本（绿色）
     * @param text 文本
     */
    public void setPassText(String text) {
        setForeground(Color.GREEN);
        setText(text);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();

        // 设置抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 获取文字信息
        String text = getText();
        Font font = getFont();
        FontMetrics metrics = g2d.getFontMetrics(font);

        // 计算文字宽高
        int textWidth = metrics.stringWidth(text);
        int textHeight = metrics.getAscent();

        // 根据对齐方式计算 x 坐标
        int x = 0;
        int y = textHeight; // 默认顶部对齐时的基线

        if (getHorizontalAlignment() == SwingConstants.CENTER) {
            x = (getWidth() - textWidth) / 2; // 居中对齐
        } else if (getHorizontalAlignment() == SwingConstants.RIGHT) {
            x = getWidth() - textWidth; // 右对齐
        }

        // 垂直居中
        if (getVerticalAlignment() == SwingConstants.CENTER) {
            y = (getHeight() + textHeight) / 2 - metrics.getDescent();
        } else if (getVerticalAlignment() == SwingConstants.BOTTOM) {
            y = getHeight() - metrics.getDescent();
        }

        // 绘制阴影
        if (shadowAlpha > 0) {
            g2d.setFont(font);
            g2d.setColor(new Color(shadowColor.getRed(), shadowColor.getGreen(), shadowColor.getBlue(),
                    (int) (shadowAlpha * 255)));
            g2d.drawString(text, x + shadowOffsetX, y + shadowOffsetY);
        }

        // 绘制主文字
        g2d.setColor(getForeground());
        g2d.drawString(text, x, y);

        g2d.dispose();
    }
}
