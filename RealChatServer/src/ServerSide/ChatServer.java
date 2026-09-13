package ServerSide;

import ServerSide.customized.SMsgProcessor;
import ServerSide.database.dataItems.FriendshipItem;
import ServerSide.database.managers.FriendshipManager;

import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class ChatServer {
    private int port = 12345;
    private final int THREAD_POOL_SIZE = 4; // 最大连接数
    private ServerSocket serverSocket = null;
    private ExecutorService executor;
    private BlockingQueue<String> messageQueue; // 消息队列，待processMessages线程处理
    private BlockingQueue<String> responseQueue; // 回应队列，待发送
    private ArrayList<ClientHandler> clientHandlers; // 维护每个连接的引用

    /**
     * 使用默认端口初始化服务端后台
     */
    public ChatServer() {
        initRest();
    }

    /**
     * 使用指定的端口初始化服务端后台
     * @param port 端口
     */
    public ChatServer(int port) {
        this.port = port;
        initRest();
    }

    private void initRest() {
        this.executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        this.messageQueue = new LinkedBlockingQueue<>();
        this.responseQueue = new LinkedBlockingQueue<>();
        this.clientHandlers = new ArrayList<>(THREAD_POOL_SIZE);
    }

    public int getPort() {
        return port;
    }

    /**
     * 根据uid查找并返回clientHandler，若找不到则 null
     */
    public ClientHandler findByUid(String uid) {
        ClientHandler clientHandler = null;
        for (ClientHandler c : clientHandlers) {
            if (uid.equals(c.getUid())) {
                clientHandler = c;
                break;
            }
        }
        return clientHandler;
    }


    /**
     * 启动定时遍历并清除已关闭的连接引用的线程
     */
    public void ensureLinkAlive() {
        Timer timer = new Timer(1000, null);
        timer.addActionListener(e -> clientHandlers.removeIf(c -> !c.isConnecting()));
        timer.start();
    }

    /**
     * 发送广播消息
     */
    public void sendBroadcastMessage(String msg) {
        for (ClientHandler c : clientHandlers) {
            if (c.isConnecting()) {
                c.sendMessage("SYSTEM:" + msg);
            }
        }
    }

    /**
     * 在公共聊天室发言
     */
    public void sendSelfMsgToAll(String uid, String nickname, String msg) {
        for (ClientHandler c : clientHandlers) {
            if (c.isConnecting()) {
                if (!uid.equals(c.getUid()))
                    c.sendMessage("CHAT:1:00000:" + uid + ":" + nickname + ":" + msg);
            }
        }
    }

    /**
     * 获取已登录的用户连接的列表
     */
    public List<ClientHandler> getLoggedInUserHandlers() {
        ArrayList<ClientHandler> userHandlers = new ArrayList<>();
        for (ClientHandler c : clientHandlers) {
            if (c.getUid() != null) {
                userHandlers.add(c);
            }
        }
        return userHandlers;
    }

    /**
     * 检查该 uid 对应用户是否已经登录了
     * @param uid 要检查的uid
     * @return 是否已经登录
     */
    public boolean hasUserLoggedIn(String uid) {
        boolean flag = false;
        for (ClientHandler c : clientHandlers) {
            if (uid.equals(c.getUid())) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    /**
     * 自己的状态改变，通知好友
     */
    public void sendUpdateWithUid(ClientHandler c, int status) {
        List<ClientHandler> userHandlers = getLoggedInUserHandlers();
        List<FriendshipItem> friendshipItems = FriendshipManager.DatabaseConnection.getFriendshipsByUid(c.getUid());
        List<String> friendUidList = new ArrayList<>();
        for (FriendshipItem f : friendshipItems) {
            if (f.uid1.equals(c.getUid())) {
                friendUidList.add(f.uid2);
            } else {
                friendUidList.add(f.uid1);
            }
        }

        for (ClientHandler u : userHandlers) {
            if (u.getUid() != null) {
                if (friendUidList.contains(u.getUid())) {
                    u.sendMessage("UPDATE:" + c.getUid() + ":" + status);
                    if (c.getUid() != null) {
                        c.sendMessage("UPDATE:" + u.getUid() + ":1");
                    }
                }
            }
        }
    }

    /**
     * 对 00000 在线聊天室：自己的状态改变，已登录则 设为当前登录人数 + 1；退出登录则 设为当前登录人数 - 1
     * 对 一般群聊：加入群聊则 peopleNum + 1，退出群聊则 peopleNum - 1
     */
    public void sendUpdateWithGid(String gid, int peopleNum) {
        for (ClientHandler c : clientHandlers) {
            if (c.getUid() != null) {
                c.sendMessage("UPDATE:" + gid + ":" + peopleNum);
            }
        }
    }

    /**
     * 统计在线人数
     * @return 在线人数
     */
    public int sumOnlineUsers() {
        int sum = 0;
        for (ClientHandler c : clientHandlers) {
            if (c.isConnecting()) {
                sum++;
            }
        }
        return sum;
    }

    /**
     * 统计已登录的人数
     * @return 登录人数
     */
    public int sumLoggedInUsers() {
        int sum = 0;
        for (ClientHandler c : clientHandlers) {
            if (c.getUid() != null) {
                sum++;
            }
        }
        return sum;
    }

    /**
     * 开启服务
     */
    public void startService() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("服务器启动，等待客户端连接...");
            ensureLinkAlive();
        } catch (IOException e) {
            messageQueue.offer("EXCEPTION:START_LISTENING->SERVER_START");
            System.err.println("服务器启动异常！\n" + e.getMessage());
        }
        try {
            while (true) {
                Socket clientSocket = serverSocket.accept(); // 接受客户端连接
                System.out.println("客户端已连接：" + clientSocket.getInetAddress());

                // 为每个客户端开启一个新线程，使用线程池处理
                ClientHandler clientHandler = new ClientHandler(this, clientSocket, messageQueue);
                clientHandlers.add(clientHandler);
                executor.execute(clientHandler);
                SMsgProcessor.transferLog("客户端已连接：" + clientSocket.getInetAddress() + " | 当前在线人数：" +
                        sumOnlineUsers());
            }
        } catch (IOException e) {
            messageQueue.offer("EXCEPTION:START_LISTENING->CLIENT_SOCKET");
            System.err.println("客户端连接建立异常！\n" + e.getMessage());
        }
    }

}



