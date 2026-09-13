package ClientSide.customized;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * <h2>头像上传器</h2>
 * 注意uploadAvatarLabel的属性，自行设置。
 */
public class AvatarUploader {
    private JLabel uploadAvatarLabel; // 标签
    private BufferedImage currentAvatar; // 当前头像图像

    /**
     * 使用文件路径初始化一个头像上传器
     * @param defaultUploadAvatar 默认显示的头像路径文件名
     */
    public AvatarUploader(String defaultUploadAvatar) {
        // 初始化标签
        Image defaultAvatar = new ImageIcon(defaultUploadAvatar).getImage().getScaledInstance(Dealer.s(48),
                Dealer.s(48), Image.SCALE_DEFAULT);
        this.uploadAvatarLabel = new JLabel(new ImageIcon(defaultAvatar));
        this.uploadAvatarLabel.setPreferredSize(new Dimension(48, 48));


        // 添加点击事件
        this.uploadAvatarLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleAvatarUpload();
            }
        });
    }

    public JLabel getUploadAvatarLabel() {
        return uploadAvatarLabel;
    }

    private void handleAvatarUpload() {
        // 创建文件选择器
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("选择头像");
        fileChooser.setFileFilter(new FileNameExtensionFilter("图像文件 (*.png, *.jpg, *.jpeg)",
                "png", "jpg", "jpeg"));

        // 打开选择器并获取用户操作结果
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                // 读取图像
                BufferedImage originalImage = ImageIO.read(selectedFile);

                // 裁剪并缩放
                BufferedImage processedImage = cropAndResizeImage(originalImage, Dealer.s(48), Dealer.s(48));

                // 更新当前头像
                currentAvatar = processedImage;

                // 显示在标签中
                uploadAvatarLabel.setIcon(new ImageIcon(processedImage));
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "无法加载图片：" + ex.getMessage(),
                        "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private BufferedImage cropAndResizeImage(BufferedImage original, int targetWidth, int targetHeight) {
        int width = original.getWidth();
        int height = original.getHeight();

        // 计算裁剪区域的起始点
        int size = Math.min(width, height); // 使用最小边
        int x = (width - size) / 2; // 水平居中
        int y = (height - size) / 2; // 垂直居中

        // 裁剪成正方形
        BufferedImage croppedImage = original.getSubimage(x, y, size, size);

        // 创建缩放后的图像
        BufferedImage scaledImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = scaledImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(croppedImage, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();

        return scaledImage;
    }

    public BufferedImage getCurrentAvatar() {
        return currentAvatar;
    }
}
