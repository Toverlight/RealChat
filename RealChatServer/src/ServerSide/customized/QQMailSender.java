package ServerSide.customized;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class QQMailSender {

    private static String from; // 发件人邮箱地址
    private static String authCode; // 授权码

    static {
        String configPath = "config.ini";
        Properties properties = new Properties();

        try {
            // 加载配置文件
            FileInputStream inputStream = new FileInputStream(configPath);
            properties.load(inputStream);

            // 读取邮箱地址和授权码
            from = properties.getProperty("email");
            authCode = properties.getProperty("auth_code");

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("读取配置文件时出错！");
        }
    }

    public static void sendMailVC(String email, String msg) {
        // 收件人邮箱地址
        final String to = email;
        // QQ 邮箱 SMTP 服务器地址
        final String host = "smtp.qq.com";
        // 设置邮件服务器属性
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465"); // SMTP 端口号
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        // 获取默认的 Session 对象
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, authCode); // 验证发件人
            }
        });

        try {
            // 创建邮件消息
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from)); // 发件人
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to)); // 收件人
            message.setSubject("验证码邮件"); // 邮件标题
            message.setText(msg); // 邮件内容

            // 发送邮件
            Transport.send(message);
            System.out.println("邮件发送成功！");
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("邮件发送失败！");
        }
    }

    public static void main(String[] args) {
        // 输出读取的值
        System.out.println("Email: " + from);
        System.out.println("Auth Code: " + authCode);
    }
}

