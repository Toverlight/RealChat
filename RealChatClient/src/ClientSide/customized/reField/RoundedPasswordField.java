package ClientSide.customized.reField;

import ClientSide.customized.Dealer;
import ClientSide.customized.reFashion.RoundedBorder;

import javax.swing.*;
import java.awt.*;

/**
 * 圆角矩形密码框
 */
public class RoundedPasswordField extends JPasswordField {
    public RoundedPasswordField() {
        super();
        setOpaque(false); // 设置背景透明
        setBorder(new RoundedBorder(Dealer.s(20))); // 设置圆角边框
    }
    public RoundedPasswordField(int columns) {
        super(columns);
        setOpaque(false); // 设置背景透明
        setBorder(new RoundedBorder(Dealer.s(20))); // 设置圆角边框
    }

    @Override
    protected void paintComponent(Graphics g) {
        // 如果背景是透明的，JTextField默认会去绘制透明背景区域
        Graphics2D g2 = (Graphics2D) g;
        if (getBackground() != null) {
            g2.setColor(getBackground()); // 保持背景色
            g2.fillRoundRect(2, 2, getWidth() - 3, getHeight() - 3,
                    Dealer.s(20), Dealer.s(20)); // 绘制圆角背景
        }
        super.paintComponent(g); // 保持文本区域的绘制
    }
}
