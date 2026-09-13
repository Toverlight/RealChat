package ClientSide.customized.textProcessor;

import javax.swing.text.JTextComponent;

/**
 * 广播式文本设置器
 * @param <T> 文本组件
 */
public class RadioTextSetter<T extends JTextComponent> {
    private T[] batchT = null;

    public RadioTextSetter() {}
    @SafeVarargs
    public RadioTextSetter(T... ts) {
        batchT = ts;
    }

    @SafeVarargs
    public final void bind(T... ts) {
        batchT = ts;
    }

    public void radioClear() {
        if (batchT == null || batchT.length == 0) {
            return;
        }
        for (T elem : batchT) {
            elem.setText("");
        }
    }
}
