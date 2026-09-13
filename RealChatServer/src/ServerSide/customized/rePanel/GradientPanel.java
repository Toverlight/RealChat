package ServerSide.customized.rePanel;

import javax.swing.*;
import java.awt.*;

public class GradientPanel extends JPanel {

    private boolean isLeftBlue = false;

    public GradientPanel() {}
    public GradientPanel(boolean isLeftBlue) {
        this.isLeftBlue = isLeftBlue;
    }

    /**
     * 实现具有背景颜色渐变效果的面板
     */
    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        Color color1 = new Color(65, 165, 238);

        Color color2 = new Color(107, 195, 195);

        int w = getWidth();

        int h = getHeight();

        GradientPaint gp;
        if (isLeftBlue) {
            gp = new GradientPaint(0, (int)(h / 2), color1, w, (int)(h / 2), color2);
        } else {
            gp = new GradientPaint(0, (int)(h / 2), color2, w, (int)(h / 2), color1);
        }

        g2d.setPaint(gp);

        g2d.fillRect(0, 0, w, h);
    }
}
