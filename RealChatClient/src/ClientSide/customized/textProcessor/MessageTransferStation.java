package ClientSide.customized.textProcessor;

import javax.swing.text.JTextComponent;
import java.awt.*;

/**
 * 消息中转站。用来将一段消息装送至目标框或区
 * @param <T> 消息目标
 */
public class MessageTransferStation<T extends JTextComponent> {
    private String message = null;
    private T msgTarget = null;
    private Color color = null;

    public MessageTransferStation(){}
    public MessageTransferStation(String message) {
        this.message = message;
    }
    public MessageTransferStation(T msgTarget) {
        this.msgTarget = msgTarget;
    }
    public MessageTransferStation(String message, T msgTarget) {
        this.message = message;
        this.msgTarget = msgTarget;
    }

    /**
     * 绑定消息目标
     * @param msgTarget 消息目标，要展示消息的框或区
     */
    public void bind(T msgTarget) {
        this.msgTarget = msgTarget;
    }

    /**
     * 设置消息。不改变颜色
     * @param message 要中转的消息内容
     */
    public void setMessage(String message) {
        this.message = message;
        this.color = null;
    }

    /**
     * 设置普通消息，呈黑色
     * @param message 要中转的消息内容
     */
    public void setInfoMessage(String message) {
        this.message = message;
        this.color = Color.BLACK;
    }

    /**
     * 设置错误消息，呈红色
     * @param message 要中转的消息内容
     */
    public void setErrorMessage(String message) {
        this.message = message;
        this.color = Color.RED;
    }

    /**
     * 文本装送
     */
    public void transfer() {
        if (message == null || msgTarget == null) {
            return;
        }
        msgTarget.setText(message);
        if (color != null) {
            msgTarget.setForeground(color);
        }
    }

}
