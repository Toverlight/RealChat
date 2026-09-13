package ServerSide;

import ServerSide.customized.SMsgProcessor;
import ServerSide.customized.enums.protocol.UpdateStatus;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 为每个用户维持的一条连接
 */
public class ClientHandler implements Runnable {
    private ChatServer chatServer;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private BlockingQueue<String> messageQueue;

    private String uid = null;      // 若登录，则记录客户uid。
    private String tempRegUid = null;   // 对于注册成功的用户，临时保存一下 uid，待 FETCH 协议返回后，擦除

    private String proVc = null;    // 带用途标识的验证码
    private Timer proVcTimer; // 控制验证码有效期的定时器

    public ClientHandler(ChatServer chatServer, Socket socket, BlockingQueue<String> messageQueue) {
        this.chatServer = chatServer;
        this.messageQueue = messageQueue;
        this.clientSocket = socket;
        this.messageQueue = new LinkedBlockingQueue<>();

        // 验证码有效期 5 分钟
        proVcTimer = new Timer(300000, null);
        proVcTimer.setRepeats(false);
        proVcTimer.addActionListener(e -> proVc = null);
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setTempRegUid(String tempRegUid) {
        this.tempRegUid = tempRegUid;
    }

    public String getTempRegUid() {
        return tempRegUid;
    }

    /**
     * 更新验证码
     * @param vcOf 用于...的验证码
     * @param vc 新生成的验证码
     */
    public void updateProVc(int vcOf, String vc) {
        proVc = String.valueOf(vcOf) + "/" + vc;
        proVcTimer.restart();
    }

    /**
     * 获取存储的验证码（如果有效）的用途。
     * @return 非负值时是 vcOf，-1时验证码失效，-2时验证码存储形式异常（防）
     */
    public int getVcOf() {
        if (proVc == null) return -1;
        String[] parts = proVc.split("/");
        if (parts.length != 2) return -2;
        int vcOf = Integer.parseInt(parts[0]);
        return vcOf;
    }

    /**
     * 获取存储的四位验证码。
     * @return 四位验证码。若失效则 null
     */
    public String getVc() {
        if (proVc == null) return null;
        String[] parts = proVc.split("/");
        if (parts.length != 2) return null;
        return parts[1];
    }

    /**
     * 检查当前与客户端的连接是否有效
     * @return 连接是否有效
     */
    public boolean isConnecting() {
        boolean flag = false;
        if (clientSocket != null && clientSocket.isConnected() && !clientSocket.isClosed() &&
                !clientSocket.isInputShutdown() && !clientSocket.isOutputShutdown()) {
            flag = true;
        }
        return flag;
    }

    /**
     * 向客户端发送消息
     */
    public boolean sendMessage(String msg) {
        boolean flag = false;
        if (out != null && !clientSocket.isOutputShutdown()) {
            out.println(msg);
            flag = true;
        }
        return flag;
    }

    /**
     * 获取当前登录的人数
     * @return 当前登录的人数
     */
    public int getLoggedInSum() {
        return chatServer.sumLoggedInUsers();
    }

    @Override
    public void run() {
        try {
            new Thread(this::processMessages).start();
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            String received;
            while ((received = in.readLine()) != null) {
                messageQueue.offer(received);
                System.out.println("收到客户端消息: " + received);
            }
        } catch (IOException e) {
            chatServer.sendUpdateWithUid(this, UpdateStatus.OFFLINE);
            chatServer.sendUpdateWithGid("00000", getLoggedInSum());
            Timer timer = new Timer(500, null);
            timer.setRepeats(false);
            timer.addActionListener(e1 -> {
                uid = null;
            });
            timer.start();
            SMsgProcessor.transferLog("来自 " + clientSocket.getInetAddress() + " 的连接已断开：" + e.getMessage() +
                    "| 当前在线人数：" + (chatServer.sumOnlineUsers() - 1));
            System.err.println("连接或流错误：" + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("关闭连接时出错！" + e.getMessage());
            }
        }
    }

    /**
     * 处理接收到的消息的线程
     */
    public void processMessages() {
        while (true) {
            try {
                String message = messageQueue.take();
                String[] slices = message.split(":");
                switch (slices[0]) {
                    case "EXCEPTION":
                        SMsgProcessor.resolveException(slices, this);
                        break;
                    case "LOGIN":
                        SMsgProcessor.resolveLogin(slices, this);
                        break;
                    case "REGISTER":
                        SMsgProcessor.resolveRegister(slices, this);
                        break;
                    case "RETRIEVE":
                        SMsgProcessor.resolveRetrieve(slices, this);
                        break;
                    case "FETCH":
                        SMsgProcessor.resolveFetch(slices, this);
                        break;
                    case "CHAT":
                        SMsgProcessor.resolveChat(message.split(":", 5), this);
                        break;
                    case "SEARCH":
                        SMsgProcessor.resolveSearch(slices, this);
                        break;
                    case "REQUEST":
                        SMsgProcessor.resolveRequest(slices, this);
                        break;
                    default:
                        SMsgProcessor.resolveUnknown(message, this);
                        break;
                }
                // 使用 SwingUtilities.invokeLater 确保在 EDT 线程中更新 UI

            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }
}
