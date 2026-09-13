package ServerSide;

import ServerSide.customized.Dealer;
import ServerSide.customized.SMsgProcessor;
import ServerSide.customized.rePanel.GradientPanel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class ServerManagementFrame extends JFrame {

    private Font serverFont = new Font("黑体", Font.PLAIN, 8);;
    private Font editorFont = new Font("微软雅黑", Font.PLAIN, 20);
    private Font titleFont = new Font("黑体", Font.BOLD, Dealer.s(8));

    private ChatServer chatServer;

    private int width;
    private int height;

    private Container container;
    private GradientPanel listenerGradientPanel;
    private GradientPanel logGradientPanel;
    private GradientPanel broadcastGradientPanel;

    private JLabel portLabel;
    private JTextField portField;
    private JButton listenBtn;

    private JTextArea logArea;

    private JTextArea broadcastArea;
    private JButton importBtn;
    private JButton clearBtn;
    private JButton sendBtn;

    private boolean isListening = false;

    public ServerManagementFrame(String title, ChatServer chatServer) {
        this.chatServer = chatServer;
        width = 350;
        height = 420;
        setTitle(title);

        /*
        顶层
         */
        container = getContentPane();
        container.setLayout(null);

        /*
        区域
         */
        listenerGradientPanel = new GradientPanel(true);
        listenerGradientPanel.setLayout(null);
        listenerGradientPanel.setBounds(0, 0, width, 90);
        listenerGradientPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1),  // 边框
                "端口配置",  // 标题
                TitledBorder.LEFT,  // 标题位置
                TitledBorder.TOP,   // 标题位置
                titleFont  // 标题字体
        ));
        container.add(listenerGradientPanel);

        logGradientPanel = new GradientPanel(false);
        logGradientPanel.setLayout(null);
        logGradientPanel.setBounds(0, listenerGradientPanel.getHeight(), width, 150);
        logGradientPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1),  // 边框
                "日志",  // 标题
                TitledBorder.LEFT,  // 标题位置
                TitledBorder.TOP,   // 标题位置
                titleFont  // 标题字体
        ));
        container.add(logGradientPanel);

        broadcastGradientPanel = new GradientPanel(true);
        broadcastGradientPanel.setLayout(null);
        broadcastGradientPanel.setBounds(0, logGradientPanel.getY() + logGradientPanel.getHeight(),
                width, height - listenerGradientPanel.getHeight() - logGradientPanel.getHeight());
        broadcastGradientPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1),  // 边框
                "广播消息",  // 标题
                TitledBorder.LEFT,  // 标题位置
                TitledBorder.TOP,   // 标题位置
                titleFont  // 标题字体
        ));
        container.add(broadcastGradientPanel);

        /*
        控件
         */
        portLabel = new JLabel("端口：");
        portLabel.setFont(serverFont);
        portLabel.setBounds(100, 30, 40, 18);
        listenerGradientPanel.add(portLabel);

        portField = new JTextField(Integer.toString(chatServer.getPort()));
        portField.setFont(editorFont);
        portField.setEditable(false);
        portField.setBounds(portLabel.getX() + portLabel.getWidth(), portLabel.getY(), 70, 18);
        listenerGradientPanel.add(portField);

        listenBtn = new JButton("开始监听");
        listenBtn.setFont(serverFont);
        listenBtn.setBounds(portField.getX() + portField.getWidth() + 20, portLabel.getY(), 80, 20);
        listenBtn.addActionListener(e -> {
            if (isListening) {
                listenBtn.setText("开始监听");
                isListening = false;
                printLog("系统消息：已停止监听");
            } else {
                listenBtn.setText("停止监听");
                isListening = true;
                printLog("系统消息：开始监听 | 端口：" + portField.getText());
            }
        });
        listenerGradientPanel.add(listenBtn);



        logArea = new JTextArea();
        logArea.setFont(editorFont);
        logArea.setEditable(false);
        logGradientPanel.add(logArea);

        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBounds(10, 10, logGradientPanel.getWidth() - 20,
                logGradientPanel.getHeight() - 20);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        logGradientPanel.add(scrollPane);



        broadcastArea = new JTextArea();
        broadcastArea.setFont(editorFont);
        broadcastArea.setBounds(10, 10, broadcastGradientPanel.getWidth() - 30 - 60,
                broadcastGradientPanel.getHeight() - 40);
        broadcastArea.setLineWrap(true);
        broadcastArea.setWrapStyleWord(true);
        broadcastGradientPanel.add(broadcastArea);

        int verticalSpace = 10;
        importBtn = new JButton("粘 贴");
        importBtn.setFont(serverFont);
        importBtn.setBounds(broadcastArea.getX() + broadcastArea.getWidth() + 10,
                broadcastArea.getY() + 10, 60, 30);
        importBtn.addActionListener(e -> {
            broadcastArea.setText(Dealer.getClipboardText());
        });
        broadcastGradientPanel.add(importBtn);

        clearBtn = new JButton("清 空");
        clearBtn.setFont(serverFont);
        clearBtn.setBounds(importBtn.getX(), importBtn.getY() + importBtn.getHeight() + verticalSpace,
                importBtn.getWidth(), importBtn.getHeight());
        clearBtn.addActionListener(e -> {
            broadcastArea.setText("");
        });
        broadcastGradientPanel.add(clearBtn);

        sendBtn = new JButton("发 送");
        sendBtn.setFont(serverFont);
        sendBtn.setBounds(importBtn.getX(), clearBtn.getY() + clearBtn.getHeight() + verticalSpace,
                importBtn.getWidth(), importBtn.getHeight());
        sendBtn.addActionListener(e -> {
            if (!"".equals(broadcastArea.getText())) {
                chatServer.sendBroadcastMessage(broadcastArea.getText());
                SMsgProcessor.transferLog("系统消息：广播内容已发送");
                broadcastArea.setText("");
            }
        });
        broadcastGradientPanel.add(sendBtn);


        // ===

        setSize(width, height);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

    }

    public boolean isListening() {
        return isListening;
    }

    public void printLog(String log) {
        logArea.setText(logArea.getText() + log + "\n");
    }

}
