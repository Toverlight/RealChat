package ClientSide;

import ClientSide.customized.CMsgProcessor;
import ClientSide.database.dataItems.GroupItem;
import ClientSide.database.dataItems.UserItem;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChatClient {
    private String serverAddress = "127.0.0.1"; // 服务器地址
    private int serverPort = 12345; // 服务器端口
    private Socket socket = null;
    private PrintWriter out;
    private BufferedReader in;
    private ExecutorService executor;
    private AtomicBoolean running;// 用于线程间通信的标志
    private BlockingQueue<String> messageQueue; // 发送消息队列
    private BlockingQueue<String> receivedMessages; // 接收消息队列

    private String uid = null; // 登录的uid
    private String nickname = null; // 该用户的昵称
    private String email = null; // 该用户的邮箱

    private ArrayList<UserItem> friends;    // 好友列表
    private ArrayList<GroupItem> groups;    // 群组列表

    private static String tempRetUid = null; // 找回账户请求发起后，临时保存 uid，用户头像 FETCH 接收后，擦除

    /**
     * 使用默认地址和端口初始化客户端后台
     */
    public ChatClient() {
        initRest();
    }
    /**
     * 使用指定的地址和端口初始化客户端后台
     * @param serverAddress 服务器ip地址
     * @param serverPort 服务器端口
     */
    public ChatClient(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        initRest();
    }
    private void initRest() {
        this.executor = Executors.newFixedThreadPool(3);
        this.running = new AtomicBoolean(true);
        this.messageQueue = new LinkedBlockingQueue<>();
        this.receivedMessages = new LinkedBlockingQueue<>();
        friends = new ArrayList<>();
        groups = new ArrayList<>();
    }

    /**
     * 查找该ID的用户是否在好友列表中
     */
    public boolean searchIfUidInFriends(String uid) {
        boolean flag = false;
        for (UserItem u : friends) {
            if (u.uid.equals(uid)) {
                flag = true;
                break;
            }
        }
        return flag;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public static String getTempRetUid() {
        return tempRetUid;
    }

    public static void setTempRetUid(String tempRetUid) {
        ChatClient.tempRetUid = tempRetUid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void addFriendItem(UserItem userItem) {
        friends.add(userItem);
    }

    public void addGroupItem(GroupItem groupItem) {
        groups.add(groupItem);
    }

    public ArrayList<UserItem> getFriends() {
        return friends;
    }

    public ArrayList<GroupItem> getGroups() {
        return groups;
    }

    /**
     * 检查用户是否在好友列表中
     * @param uid 要检查的用户uid
     * @return 是否在好友列表中
     */
    public boolean checkIfUserInFriends(String uid) {
        boolean flag = false;
        for (UserItem u : friends) {
            if (uid.equals(u.uid)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    /**
     * 检查群组是否在群组列表中
     * @param gid 要检查的群组gid
     * @return 是否在群组列表中
     */
    public boolean checkIfGroupInGroups(String gid) {
        boolean flag = false;
        for (GroupItem g : groups) {
            if (gid.equals(g.gid)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    private void roughConnect() throws IOException{
        socket = new Socket(serverAddress, serverPort);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        if (executor.isShutdown()) {
            this.executor = Executors.newFixedThreadPool(3);
        }

        // 启动处理消息线程
        executor.execute(this::processMessages);
        // 启动发送线程
        executor.execute(this::sendMessages);
        // 启动接收线程
        executor.execute(this::receiveMessages);
        // 启动心跳检测线程
//        executor.execute(this::keepConnectingRegularly);
    }

    /**
     * 尝试与服务器建立连接
     * @return 是否连接成功
     */
    public boolean connect() {
        boolean flag = true;
        try {
            roughConnect();
        } catch (IOException e) {
            flag = false;
            receivedMessages.offer("EXCEPTION:CONNECT:" + e.getMessage());
        }
        return flag;
    }

    /**
     * 带参数的尝试与服务器建立连接
     * @param funcName 函数名称
     * @return 是否连接成功
     */
    public boolean connect(String funcName) {
        boolean flag = true;
        try {
            roughConnect();
        } catch (IOException e) {
            flag = false;
            receivedMessages.offer("EXCEPTION:CONNECT->" + funcName + ":" + e.getMessage());
        }
        return flag;
    }


    /**
     * 尝试重连：关闭连接，阻塞1.1秒后，尝试建立新的连接
     * @return 是否连接成功
     */
    public boolean reconnect() {
        boolean flag = true;
        try {
            close();
            Thread.sleep(1100);
            if (!connect()) flag = false;
        } catch (InterruptedException e) {
            flag = false;
            receivedMessages.offer("EXCEPTION:RECONNECT->INTERRUPTED:" + e.getMessage());
        }
        return flag;
    }

    /**
     * 带标识串的尝试重连：关闭连接，阻塞1.1秒后，尝试建立新的连接
     * @param funcName 函数名称
     * @return 是否连接成功
     */
    public boolean reconnect(String funcName) {
        boolean flag = true;
        try {
            close();
            Thread.sleep(1100);
            if (!connect(funcName)) flag = false;
        } catch (InterruptedException e) {
            flag = false;
            receivedMessages.offer("EXCEPTION:RECONNECT->" + funcName + ":" + e.getMessage());
        }
        return flag;
    }

    /**
     * 检查当前与服务器的连接是否有效
     * @return 连接是否有效
     */
    public boolean isConnecting() {
        boolean flag = false;
        if (socket != null && socket.isConnected() && !socket.isClosed() &&
            !socket.isInputShutdown() && !socket.isOutputShutdown()) {
            flag = true;
        }
        return flag;
    }

    /**
     * 处理接收到的消息的线程
     */
    public void processMessages() {
        while (true) {
            try {
                String message = receivedMessages.take();
                System.out.println("收到服务器消息：" + message);
                String[] slices = message.split(":");
                switch (slices[0]) {
                    case "EXCEPTION":
                        CMsgProcessor.resolveException(slices);
                        break;
                    case "RECONNECTED":
                        CMsgProcessor.resolveReconnected(slices);
                        break;
                    case "LOGIN":
                        CMsgProcessor.resolveLogin(message.split(":", 5));
                        break;
                    case "REGISTER":
                        CMsgProcessor.resolveRegister(slices);
                        break;
                    case "RETRIEVE":
                        CMsgProcessor.resolveRetrieve(slices);
                        break;
                    case "FETCH":
                        CMsgProcessor.resolveFetch(message.split(":", 3));
                        break;
                    case "SYSTEM":
                        CMsgProcessor.resolveSystemMessage(slices);
                        break;
                    case "UPDATE":
                        CMsgProcessor.resolveUpdate(slices);
                        break;
                    case "CHAT":
                        CMsgProcessor.resolveChat(message.split(":", 6));
                        break;
                    case "SEARCH":
                        CMsgProcessor.resolveSearch(slices);
                        break;
                    case "REQUEST":
                        CMsgProcessor.resolveRequest(slices);
                        break;
                    default:
                        CMsgProcessor.resolveUnknown(message);
                        break;
                    // 使用 SwingUtilities.invokeLater 确保在 EDT 线程中更新 UI
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * 发送消息线程
     */
    private void sendMessages() {
        try {
            while (running.get()) {
                String message = messageQueue.take(); // 阻塞直到有新消息
                out.println(message); // 发送消息
                /*if ("quit".equalsIgnoreCase(message)) {
                    running.set(false); // 退出发送线程
                }*/
            }
        } catch (InterruptedException e) {
            receivedMessages.offer("EXCEPTION:SEND_MESSAGES:" + e.getMessage());
            System.err.println("发送线程异常: " + e.getMessage());
            running.set(false);
        }
    }

    /**
     * 接收消息线程
     */
    private void receiveMessages() {
        try {
            String serverMessage;
            while (running.get() && (serverMessage = in.readLine()) != null) {
                // 将接收到的消息放入队列，传递给 UI 线程
                receivedMessages.offer(serverMessage);
            }
        } catch (IOException e) {
            receivedMessages.offer("EXCEPTION:RECEIVE_MESSAGES:" + e.getMessage());
            System.err.println("接收线程异常: " + e.getMessage());
            running.set(false);
        }
    }

    /**
     * 发送消息
     * @param msg 消息
     */
    public void sendOffer(String msg) {
        messageQueue.offer(msg);
    }

    /**
     * 心跳检测
     * @return 接收到的比特数（-1代表接收失败，其他值是成功情况）
     * @throws IOException I/O异常
     */
    private int heartbeatTest() throws IOException{
        Socket hbSocket = new Socket(serverAddress, serverPort);
        hbSocket.setSoTimeout(5000); // 设置读取超时时间为5秒

        // 发送心跳消息
        OutputStream outputStream = hbSocket.getOutputStream();
        outputStream.write("HEARTBEAT".getBytes());

        // 读取响应
        InputStream inputStream = hbSocket.getInputStream();
        byte[] buffer = new byte[1024];
        int bytesRead = inputStream.read(buffer);

        if (bytesRead == -1) {
            System.out.println("No response from server, socket might be closed.");
        } else {
            System.out.println("Received heartbeat response.");
        }

        return bytesRead;
    }

    /**
     * 心跳检测和重连机制以保持连接的线程
     */
    public void keepConnectingRegularly() {
        Timer timer = new Timer(10000, null);
        timer.addActionListener(e -> {
            int bytesRead;
            try {
                bytesRead = heartbeatTest();
                if (bytesRead == -1) {
                    reconnect();
                    receivedMessages.offer("RECONNECTED:KEEP_CONNECTING_REGULARLY");
                    timer.restart();
                }
            } catch (IOException ex) {
                receivedMessages.offer("EXCEPTION:KEEP_CONNECTING_REGULARLY:" + ex.getMessage());
                System.out.println("心跳检测或连接异常：" + ex.getMessage());
            }
        });
        timer.start();
    }


    /**
     * 尝试关闭连接，若超过1秒没关闭，则强制关闭
     */
    public void close() {
        try {
            // 关闭流线程
            executor.shutdown();
            if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
            // 关闭当前的 Socket 和其流
            if (socket != null) {
                socket.close(); // 关闭 Socket 连接
            }
            System.out.println("已关闭连接");
        } catch (IOException | InterruptedException e) {
            receivedMessages.offer("EXCEPTION:CLOSE:" + e.getMessage());
        }
    }


}

