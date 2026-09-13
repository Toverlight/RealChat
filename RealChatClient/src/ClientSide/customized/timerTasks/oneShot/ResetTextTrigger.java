package ClientSide.customized.timerTasks.oneShot;

import javax.swing.*;
import java.awt.*;

/**
 * 标签文本暂变触发器
 */
public class ResetTextTrigger implements Trigger{
    private int ms = 5000;
    private String interimText;
    private Color interimColor = null;

    public ResetTextTrigger(String interimText) {
        this.interimText = interimText;
    }
    public ResetTextTrigger(String interimText, int ms) {
        this.ms = ms;
        this.interimText = interimText;
    }
    public ResetTextTrigger(String interimText, Color interimColor) {
        this.interimColor = interimColor;
        this.interimText = interimText;
    }
    public ResetTextTrigger(String interimText, Color interimColor, int ms) {
        this.ms = ms;
        this.interimColor = interimColor;
        this.interimText = interimText;
    }

    public void setInterimColor(Color interimColor) {
        this.interimColor = interimColor;
    }

    public void trig(JLabel label) {
        String originalText = label.getText();
        label.setText(interimText);
        Color originalColor = label.getForeground();
        if (interimColor != null) {
            label.setForeground(interimColor);
        }
        Timer timer = new Timer(ms, null);
        timer.addActionListener(e -> {
            label.setText(originalText);
            label.setForeground(originalColor);
            timer.stop();
        });
        timer.start();
    }
}
