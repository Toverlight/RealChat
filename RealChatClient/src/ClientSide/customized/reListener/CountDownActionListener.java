package ClientSide.customized.reListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public abstract class CountDownActionListener implements ActionListener {
    private boolean isCountingDown = false; // 防止重复启动倒计时
    private JButton button;

    public CountDownActionListener(JButton b) {
        button = b;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isCountingDown) {
            return;
        }

        if (checkProcess()) {
            requestSendVC();
            startCountDown(button);
        }
    }

    /**
     * 检查处理
     * @return 检查是否通过。若通过则发送验证码请求，否则该函数后什么也不做。
     */
    public abstract boolean checkProcess();

    /**
     * 请求发送验证码
     */


    private void startCountDown(JButton button) {
        isCountingDown = true; // 标记为倒计时中
        String originalText = button.getText(); // 保存按钮原始文本
        button.setEnabled(false); // 按钮禁用
        button.setText(originalText + " (30)");

        Timer timer = new Timer(1000, null); // 创建一个1秒间隔的计时器
        final int[] remainingTime = {30}; // 使用数组包装以实现可变性

        timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                remainingTime[0]--; // 每秒减少1秒
                if (remainingTime[0] > 0) {
                    // 更新按钮文本为“原文本 + (剩余秒数)”
                    button.setText(originalText + " (" + remainingTime[0] + ")");
                } else {
                    // 倒计时结束，恢复按钮状态
                    timer.stop();
                    button.setEnabled(true);
                    button.setText(originalText); // 恢复原始文本
                    isCountingDown = false; // 重置倒计时状态
                }
            }
        });

        timer.start(); // 启动计时器
    }

    /**
     * 请求发送验证码（生成并发送验证码）
     */
    public void requestSendVC() {
        // 生成一个4位验证码
        String verificationCode = generateVerificationCode();
        // 在这里可以调用真实的验证码发送逻辑（例如发送邮件或短信）
        System.out.println("验证码是: " + verificationCode);
    }

    /**
     * 生成一个四位验证码
     */
    private String generateVerificationCode() {
        Random random = new Random();
        int code = 1000 + random.nextInt(9000); // 生成一个四位数的验证码
        return String.valueOf(code);
    }
}
