package ClientSide.reference;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * @Author: EDZ
 * @Description: ${description}
 * @Date: 2019/5/9 13:05
 * @Version: 1.0
 */
public class CopeImageUtil {
    public static BufferedImage cutHeadImages(String headFilename, int sideLen) {
        BufferedImage avatarImage;
        try {
            avatarImage = ImageIO.read(new File(headFilename));
            avatarImage = scaleByPercentage(avatarImage, sideLen,  sideLen);
            // 透明底的图片
            BufferedImage formatAvatarImage = new BufferedImage(sideLen, sideLen, BufferedImage.TYPE_4BYTE_ABGR);
            Graphics2D graphics = formatAvatarImage.createGraphics();
            //把图片切成一个圆
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            //留一个像素的空白区域，这个很重要，画圆的时候把这个覆盖
            int border = 1;
            //图片是一个圆型
            Ellipse2D.Double shape = new Ellipse2D.Double(border, border, sideLen - border * 2,
                    sideLen - border * 2);
            //需要保留的区域
            graphics.setClip(shape);
            graphics.drawImage(avatarImage, border, border, sideLen - border * 2, sideLen - border * 2,
                    null);
            graphics.dispose();
            //在圆图外面再画一个圆
            //新创建一个graphics，这样画的圆不会有锯齿
            graphics = formatAvatarImage.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int border1 = 3;
            //画笔是4.5个像素，BasicStroke的使用可以查看下面的参考文档
            //使画笔时基本会像外延伸一定像素，具体可以自己使用的时候测试
            Stroke s = new BasicStroke(5F, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            graphics.setStroke(s);
            graphics.setColor(Color.WHITE);
            graphics.drawOval(border1, border1, sideLen - border1 * 2, sideLen - border1 * 2);
            graphics.dispose();
            //OutputStream os = new FileOutputStream("C:\\Users\\EDZ\\Desktop\\剪裁图片\\13000.png");
            //发布项目时，如：Tomcat 他会在服务器本地tomcat webapps文件下创建此文件名
            //ImageIO.write(formatAvatarImage, "PNG", os);
            return formatAvatarImage;
        } catch (Exception e) {

            e.printStackTrace();
        }
        return null;
    }

    /**
     * 缩小Image，此方法返回源图像按给定宽度、高度限制下缩放后的图像
     *
     * @param inputImage
     *            ：压缩后宽度
     *            ：压缩后高度
     * @throws java.io.IOException
     *             return
     */
    public static BufferedImage scaleByPercentage(BufferedImage inputImage, int newWidth, int newHeight){
        // 获取原始图像透明度类型
        try {
            int type = inputImage.getColorModel().getTransparency();
            int width = inputImage.getWidth();
            int height = inputImage.getHeight();
            // 开启抗锯齿
            RenderingHints renderingHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // 使用高质量压缩
            renderingHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            BufferedImage img = new BufferedImage(newWidth, newHeight, type);
            Graphics2D graphics2d = img.createGraphics();
            graphics2d.setRenderingHints(renderingHints);
            graphics2d.drawImage(inputImage, 0, 0, newWidth, newHeight, 0, 0, width, height,
                    null);
            graphics2d.dispose();
            return img;

        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    public static void main(String[] args) throws Exception {
//        BufferedImage bf = cutHeadImages("Client/res/images/icons/xixi.png", 200);
//        if (bf == null) {
//            System.out.println("bf == null !");
//            return;
//        }
//
//        JFrame frame = new JFrame();
//        frame.setSize(600, 600);
//        frame.setLocationRelativeTo(null);
//
//        JLabel avatarLabel = new JLabel(new ImageIcon(bf));
//        avatarLabel.setBounds(100, 100, 200, 200);
//
//        Container container = frame.getContentPane();
//        container.setLayout(null);
//        container.add(avatarLabel);
//
//        frame.setVisible(true);
//    }
}
