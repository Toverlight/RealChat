package ClientSide.customized.reLabel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class CircularImageLabel extends JLabel {
    private ImageIcon imageIcon;

    public CircularImageLabel(ImageIcon icon) {
        this.imageIcon = icon;
        this.setOpaque(false);
    }

    public void setImageIcon(ImageIcon icon) {
        this.imageIcon = icon;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (imageIcon == null) {
            super.paintComponent(g);
            return;
        }

        int diameter = Math.min(getWidth(), getHeight()); // 确定圆形的直径
        Image scaledImage = imageIcon.getImage().getScaledInstance(diameter, diameter, Image.SCALE_SMOOTH);

        BufferedImage circularImage = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = circularImage.createGraphics();

        // 抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 绘制圆形裁剪区域
        g2d.setClip(new java.awt.geom.Ellipse2D.Double(0, 0, diameter, diameter));
        g2d.drawImage(scaledImage, 0, 0, diameter, diameter, null);
        g2d.dispose();

        // 将圆形图片绘制到组件中
        g.drawImage(circularImage, (getWidth() - diameter) / 2, (getHeight() - diameter) / 2, null);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(100, 100); // 默认大小
    }

//    public static void main(String[] args) {
//        JFrame frame = new JFrame("Circular Image Label Test");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setSize(800, 800);
//        frame.setLayout(null);
//
//        // 加载测试图片
//        ImageIcon icon = new ImageIcon("Client/res/images/icons/xixi.png"); // 替换为实际图片路径
//
//        int status = icon.getImageLoadStatus();
//
//        // 输出状态
//        switch (status) {
//            case MediaTracker.COMPLETE:
//                System.out.println("图片加载完成！");
//                break;
//            case MediaTracker.ERRORED:
//                System.out.println("图片加载出错！");
//                break;
//            case MediaTracker.ABORTED:
//                System.out.println("图片加载被中止！");
//                break;
//            case MediaTracker.LOADING:
//                System.out.println("图片正在加载！");
//                break;
//            default:
//                System.out.println("未知状态！");
//        }
//
//        Container container = frame.getContentPane();
//
//        // 创建自定义标签
//        CircularImageLabel circularLabel = new CircularImageLabel(icon);
//        circularLabel.setBounds(100, 100, 300, 300); // 设置标签大小
//
//        container.add(circularLabel);
//
//        frame.setVisible(true);
//    }

}

