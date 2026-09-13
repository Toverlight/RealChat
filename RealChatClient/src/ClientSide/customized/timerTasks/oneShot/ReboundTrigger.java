package ClientSide.customized.timerTasks.oneShot;

import javax.swing.*;

/**
 * 按钮定时回弹触发器
 */
public class ReboundTrigger implements Trigger{
    private int ms = 10000;
    private String interimText = null;
    private Timer timer;
    private JButton button;
    private String oriText;

    public ReboundTrigger() {}
    public ReboundTrigger(int ms) { this.ms = ms; }
    public ReboundTrigger(String interimText) {
        this.interimText = interimText;
    }
    public ReboundTrigger(String interimText, int ms) {
        this.ms = ms;
        this.interimText = interimText;
    }

    /**
     * 终止定时事件并立即恢复原状
     */
    public void stopNow() {
        timer.stop();
        if (button != null) {
            if (interimText != null)
                button.setText(oriText);
            button.setEnabled(true);
        }
    }

    public void trig(JButton button) {
        this.button = button;
        this.oriText = button.getText();
        button.setEnabled(false);
        if (interimText != null) {
            button.setText(interimText);
        }
        timer = new Timer(ms, null); // 创建一个ms毫秒间隔的计时器
        timer.addActionListener(e -> {
            button.setEnabled(true);
            button.setText(oriText);
            timer.stop();
        });
        timer.start();
    }
}
