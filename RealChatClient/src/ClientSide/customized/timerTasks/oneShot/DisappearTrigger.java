package ClientSide.customized.timerTasks.oneShot;

import javax.swing.*;

/**
 * 定时设置不可见的触发器
 */
public class DisappearTrigger implements Trigger{
    private int ms = 3000;

    public DisappearTrigger() {}
    public DisappearTrigger(int ms) { this.ms = ms; }

    public void trig(JComponent component) {
        component.setVisible(true);
        Timer timer = new Timer(ms, null); // 创建一个ms毫秒间隔的计时器
        timer.addActionListener(e -> {
            component.setVisible(false);
            timer.stop();
        });
        timer.start();
    }
}
