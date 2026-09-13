package ClientSide.customized.rePanel;

import javax.swing.*;
import java.awt.*;

public class GradientPanel extends JPanel {

    /**
     * 实现具有背景颜色渐变效果的面板
     */
    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        Color color1 = new Color(215, 222, 234);

        Color color2 = new Color(94, 138, 226);

        int w = getWidth();

        int h = getHeight();

        GradientPaint gp = new GradientPaint(0, 0, color1, w, h, color2);

        g2d.setPaint(gp);

        g2d.fillRect(0, 0, w, h);
    }
}
