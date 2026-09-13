package ClientSide.customized.reFashion;

import javax.swing.border.AbstractBorder;
import java.awt.*;

/**
 * 圆角矩形边框
 */
public class RoundedBorder extends AbstractBorder {
    private int radius; // 圆角的半径

    public RoundedBorder(int radius) {
        this.radius = radius;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 设置边框颜色
        g2.setColor(Color.GRAY);
        // 设置边框厚度
        g2.setStroke(new BasicStroke(6));
        // 绘制圆角矩形
        g2.drawRoundRect(x + 4, y + 4, width - 8, height - 8, radius, radius);
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(5, 5, 5, 5); // 边框的内边距，可以根据需要调整
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.left = 5;
        insets.top = 5;
        insets.right = 5;
        insets.bottom = 5;
        return insets;
    }
}
