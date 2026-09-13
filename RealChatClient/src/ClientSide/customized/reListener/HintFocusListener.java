package ClientSide.customized.reListener;

import ClientSide.customized.enums.HintTarget;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * 提示聚焦监听器
 */
public class HintFocusListener implements FocusListener {
    private String hintText;
    private HintTarget target;
    private JTextField textField;
    private JTextArea textArea;
    public HintFocusListener(JTextField jTextField, String hintText) {
        target = HintTarget.TEXT_FIELD;
        this.textField = jTextField;
        this.hintText = hintText;
        jTextField.setText(hintText);  //默认直接显示
        jTextField.setForeground(Color.GRAY);
    }
    public HintFocusListener(JTextArea jTextArea, String hintText) {
        target = HintTarget.TEXT_AREA;
        this.textArea = jTextArea;
        this.hintText = hintText;
        jTextArea.setText(hintText);  //默认直接显示
        jTextArea.setForeground(Color.GRAY);
    }

    @Override
    public void focusGained(FocusEvent e) {
        //获取焦点时，清空提示内容
        String temp;
        switch (target) {
            case TEXT_FIELD:
                 temp = textField.getText();
                if(temp.equals(hintText)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
                break;
            case TEXT_AREA:
                temp = textArea.getText();
                if(temp.equals(hintText)) {
                    textArea.setText("");
                    textArea.setForeground(Color.BLACK);
                }
                break;
        }

    }

    @Override
    public void focusLost(FocusEvent e) {
        //失去焦点时，没有输入内容，显示提示内容
        String temp;
        switch (target) {
            case TEXT_FIELD:
                temp = textField.getText();
                if(temp.equals("")) {
                    textField.setForeground(Color.GRAY);
                    textField.setText(hintText);
                }
                break;
            case TEXT_AREA:
                temp = textArea.getText();
                if(temp.equals("")) {
                    textArea.setForeground(Color.GRAY);
                    textArea.setText(hintText);
                }
                break;
        }


    }

}
