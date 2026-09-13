package ClientSide.customized.fileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class ImageUtil {

    /**
     * 将图像文件转换为 Base64 编码的字符串，基于 Client/res/images/ 下
     * @param imagePath 图像文件路径
     * @return 图像文件的 Base64 编码字符串
     */
    public static String encodeImageToBase64(String imagePath) {
        try {
            // 将图像文件转换为字节数组
            byte[] imageBytes = Files.readAllBytes(Paths.get("Client/res/images/" + imagePath));

            // 将字节数组编码为 Base64 字符串并返回
            return Base64.getEncoder().encodeToString(imageBytes);

        } catch (IOException e) {
            e.printStackTrace();
            return null;  // 出现异常时返回 null
        }
    }

    public static String convertToBase64(BufferedImage image) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // 将 BufferedImage 写入 ByteArrayOutputStream
            ImageIO.write(image, "png", baos);
            baos.flush();

            // 获取字节数组
            byte[] imageBytes = baos.toByteArray();

            // 将字节数组转换为 Base64 编码的字符串
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将 Base64 编码的字符串解码为图像文件，存到 Client/res/images/ 下
     * @param base64String Base64 编码的字符串
     * @param outputPath 输出文件路径
     */
    public static void decodeBase64ToImage(String base64String, String outputPath) {
        try {
            // 解码 Base64 字符串为字节数组
            byte[] imageBytes = Base64.getDecoder().decode(base64String);

            // 将字节数组写入文件
            try (FileOutputStream fos = new FileOutputStream("Client/res/images/" + outputPath)) {
                fos.write(imageBytes);
                System.out.println("图像已保存到: " + outputPath);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

