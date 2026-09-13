package ServerSide.customized;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

public class Dealer {

    private static final double scrWidth = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    private static final boolean adjust = ((int)scrWidth != 1024);
    public static final double scale = scrWidth / 1024.0;

    /**
     * 设置全局字体
     */
    public static void initGlobalFontSetting(Font fnt){
        FontUIResource fontRes = new FontUIResource(fnt);
        for(Enumeration<Object> keys = UIManager.getDefaults().keys(); keys.hasMoreElements();){
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if(value instanceof FontUIResource)
                UIManager.put(key, fontRes);
        }
    }
    /**
     * 按比例缩放图片
     * @param originalIcon 原始图片的 ImageIcon
     * @param scaleFactor 缩放倍数
     * @return 缩放后的 ImageIcon
     */
    public static ImageIcon scaleImage(ImageIcon originalIcon, double scaleFactor) {
        // 获取原始图片
        Image originalImage = originalIcon.getImage();

        // 计算缩放后的宽高
        int newWidth = (int) (originalImage.getWidth(null) * scaleFactor);
        int newHeight = (int) (originalImage.getHeight(null) * scaleFactor);

        // 缩放图片
        Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);

        // 返回新的 ImageIcon
        return new ImageIcon(scaledImage);
    }

    /**
     * 调整所有组件的大小、位置和字体
     * @param container 容器对象
     * @param scale 缩放倍数
     */
    public static void resizeComponents(Container container, double scale) {
        for (Component component : container.getComponents()) {
            // 缩放位置和大小
            int newX = (int) (component.getX() * scale);
            int newY = (int) (component.getY() * scale);
            int newWidth = (int) (component.getWidth() * scale);
            int newHeight = (int) (component.getHeight() * scale);
            component.setBounds(newX, newY, newWidth, newHeight);

            // 缩放字体
            if (component instanceof JLabel || component instanceof JButton) {
                Font font = component.getFont();
                float newSize = (float) (font.getSize() * scale);
                component.setFont(font.deriveFont(newSize));
            }

            // 如果组件是容器，递归调整其子组件
            if (component instanceof Container) {
                resizeComponents((Container) component, scale);
            }
        }
    }

    /**
     *  设置窗体图标
     */
    public static void setImageIcon(Window window, String path_file) {
        ImageIcon imageIcon = new ImageIcon(path_file);
        window.setIconImage(imageIcon.getImage());
    }

    /**
     * 窗口大小布局根据屏幕分辨率自动适应
     */
    public static void adjustScreen(Window window) {
        if (adjust) {
            resizeComponents(window, scale);
            window.setSize((int) (window.getWidth() * scale), (int) (window.getHeight() * scale));
        }
    }

    /**
     * 以特定倍率缩放值，用于窗体组件布局
     */
    public static int s(int val) {
        return (int)(val * scale);
    }

    /**
     * 设置按钮透明
     */
    public static <T extends AbstractButton> void setButtonTransparent(T button) {

        button.setMargin(new Insets(0,0,0,0));    //将边框外的上下左右空间设置为0
        button.setIconTextGap(0);                                       //将标签中显示的文本和图标之间的间隔量设置为0
        button.setBorderPainted(false);                                 //不打印边框
        button.setBorder(null);                                         //除去边框
//      button.setText(null);                                           //除去按钮的默认名称
        button.setFocusPainted(false);                                  //除去焦点的框
        button.setContentAreaFilled(false);                             //除去默认的背景填充
    }

    /**
     * 用默认浏览器打开url
     */
    public static void openWebPage(String str) {
        if (Desktop.isDesktopSupported()) {
            try {
                URI uri = new URI(str);
                Desktop.getDesktop().browse(uri);
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置窗口为中央显示
     * @param component 任意控件，包括但不限于窗口对象
     */
    public static void centerWindow(Component component)   {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension scnSize = toolkit.getScreenSize();
        int width = component.getWidth();
        int height = component.getHeight();

        component.setLocation(scnSize.width / 2 - (width / 2), scnSize.height / 2 - (height / 2));
    }

    /**
     * 设置组件的PreferredSize、MaximumSize、MinimumSize为相同值
     * @param component 要设置的组件
     * @param width 预期宽度
     * @param height 预期高度
     */
    public static void setTripleSizes(Component component, int width, int height) {
        component.setPreferredSize(new Dimension(width, height));
        component.setMaximumSize(new Dimension(width, height));
        component.setMinimumSize(new Dimension(width, height));
    }

    /**
     * 窗体风格适配操作系统
     */
    public static void adaptToOS() {
        try {
            String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 当鼠标在组件上方时，临时变成小手形状
     * @param component 任意组件
     */
    public static void addHoverMouseListenerFor(Component component) {
        component.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 点击事件
            }

            @Override
            public void mousePressed(MouseEvent e) {
                // 按下事件
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // 松开事件
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // 鼠标进入事件
                component.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // 鼠标退出事件
                component.setCursor(Cursor.getDefaultCursor());
            }
        });
    }

    /**
     * 获取剪贴板中的文本
     * @return 剪贴板中的文本
     */
    public static String getClipboardText() {
        String result = null;
        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable content = clipboard.getContents(null);

            // 检查剪贴板的内容是否是文本类型
            if (content != null && content.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                result = (String) content.getTransferData(DataFlavor.stringFlavor);
            }
        } catch (UnsupportedFlavorException | IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 将时间戳转换为格式化的日期时间字符串
     * @param timestamp 时间戳
     * @return 格式化的日期时间字符串
     */
    public static String convertTimestampToString(long timestamp) {
        // 创建一个SimpleDateFormat对象，指定日期格式
        SimpleDateFormat sdf = new SimpleDateFormat("【yyyy-MM-dd HH:mm:ss】");

        // 将时间戳转换为Date对象
        Date date = new Date(timestamp);

        // 格式化并返回日期字符串
        return sdf.format(date);
    }

    /**
     * 将时间戳转换为格式化的时间字符串
     * @param timestamp 时间戳
     * @return 格式化的时间字符串
     */
    public static String convertTimestampToStringWithoutDate(long timestamp) {
        // 创建一个SimpleDateFormat对象，指定日期格式
        SimpleDateFormat sdf = new SimpleDateFormat("【HH:mm:ss】");

        // 将时间戳转换为Date对象
        Date date = new Date(timestamp);

        // 格式化并返回日期字符串
        return sdf.format(date);
    }

    /**
     * 将时间戳转换为格式化的时间字符串
     * @param timestamp 时间戳
     * @return 格式化的时间字符串
     */
    public static String convertTimestampToStringWithoutDateSS(long timestamp) {
        // 创建一个SimpleDateFormat对象，指定日期格式
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        // 将时间戳转换为Date对象
        Date date = new Date(timestamp);

        // 格式化并返回日期字符串
        return sdf.format(date);
    }

}
