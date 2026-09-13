package ClientSide;

import ClientSide.customized.AvatarUploader;
import ClientSide.customized.CMsgProcessor;
import ClientSide.customized.Dealer;
import ClientSide.customized.checker.*;
import ClientSide.customized.fileUtils.ImageUtil;
import ClientSide.customized.reField.RoundedPasswordField;
import ClientSide.customized.reField.RoundedTextField;
import ClientSide.customized.reLabel.ShadowLabel;
import ClientSide.customized.reListener.CountDownActionListener;
import ClientSide.customized.reListener.HintFocusListener;
import ClientSide.customized.rePanel.GradientPanel;
import ClientSide.customized.reWindow.NotificationPopup;
import ClientSide.customized.textProcessor.MessageTransferStation;
import ClientSide.customized.timerTasks.oneShot.DisappearTrigger;
import ClientSide.customized.timerTasks.oneShot.ReboundTrigger;
import ClientSide.reference.CopeImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class ClientLoginFrame extends JFrame {

    private ClientMainFrame clientMainFrame = null;

    private Font clientFont;        // 客户端默认字体

    private ChatClient chatClient;

    private Container container;
    private GradientPanel gradientPanel;
    private CardLayout cardLayout;
    private GridBagConstraints gbc;
    private JPanel loginPanel;
    private JPanel avatarPanel;
    private ShadowLabel statusLabel;
    private JLabel avatarLabel;
    private ShadowLabel errLoginLabel;
    private JPanel uidPanel;
    private JLabel uidLabel;
    private RoundedTextField uidField;
    private JPanel passwordPanel;
    private JLabel passwordLabel;
    private RoundedPasswordField passwordField;
    private JPanel optionPanel;
    private JButton retrievePasswordBtn;
    private JButton confirmCheckinBtn;
    private JButton jumpRegisterBtn;
    private JPanel registerPanel;
    private AvatarUploader avu;
    private JLabel uploadAvatarLabel;
    private JLabel nicknameLabel;
    private JTextField nicknameField;
    private JLabel emailLabel;
    private JTextField emailField;
    private JLabel newPasswordLabel;
    private JPasswordField newPasswordField;
    private JLabel confirmPasswordLabel;
    private JPasswordField confirmPasswordField;
    private JLabel verificationLabel;
    private JTextField verificationField;
    private JButton sendRequestBtn;
    private JButton helperButton;
    private JButton returnLoginBtn;
    private JButton submitBtn;
    private JTextField feedbackField;
    private JPanel retrievePasswordPanel;
    private JPanel pastUidPanel;
    private JPanel retVerificationPanel;
    private JPanel resetPasswordPanel;
    private JPanel retRequestPanel;
    private JLabel pastUidLabel;
    private JTextField pastUidField;
    private JLabel retVerificationLabel;
    private JTextField retVerificationField;
    private JButton retSendBtn;
    private JLabel resetPasswordLabel;
    private JPasswordField resetPasswordField;
    private JButton retReturnBtn;
    private JButton retRequestBtn;
    private JTextField retFeedbackField;
    private JPanel helperPanel;
    private JEditorPane helperEditorPane;
    private JButton helperReturnBtn;

    private static final Font blackStyleFont = new Font("微软雅黑", Font.PLAIN, 12);
    private static final Font textEditedStyleFont = new Font("微软雅黑", Font.PLAIN, Dealer.s(10));
    private static final Font notificationFont = new Font("黑体", Font.PLAIN, Dealer.s(10));
    private static final Color darkColor = new Color(15, 15, 15);

    private static final int avatarSize = Dealer.s(48);
    private static final Image helperIcon = new ImageIcon("Client/res/images/icons/helpQuestion.png").getImage().
            getScaledInstance(Dealer.s(30), Dealer.s(30), Image.SCALE_DEFAULT);

    MessageTransferStation<JTextField> mtsReg = new MessageTransferStation<>();// 注册的
    MessageTransferStation<JTextField> mtsRet = new MessageTransferStation<>();// 找回的

    public ClientLoginFrame(String title, ChatClient chatClient){
        this.clientMainFrame = new ClientMainFrame("RealChat");
        CMsgProcessor.setClientMainFrame(clientMainFrame);
        Dealer.setImageIcon(clientMainFrame, "Client/res/images/icons/bell.png");
        this.chatClient = chatClient;
        this.clientFont = new Font("黑体", Font.PLAIN, 10);
        setTitle(title);

        container = getContentPane();

        gradientPanel = new GradientPanel();
        gradientPanel.setBounds(0, 0, container.getWidth(), container.getHeight());
        gradientPanel.setFocusable(true);

        cardLayout = new CardLayout();
        gradientPanel.setLayout(cardLayout);

        gbc = new GridBagConstraints();

        // ===
        // ---登录板

        loginPanel = new JPanel();
        loginPanel.setLayout(new GridBagLayout());
        loginPanel.setOpaque(false);

        // 头像显示

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 2.8;
        gbc.fill = GridBagConstraints.BOTH;

        avatarPanel = new JPanel();
        avatarPanel.setOpaque(false);
        avatarPanel.setLayout(null);

        loginPanel.add(avatarPanel, gbc);

        statusLabel = new ShadowLabel("");
        statusLabel.setFont(clientFont);
        statusLabel.setBounds(50, 5, 200, 18);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setForeground(Color.RED);

        avatarPanel.add(statusLabel);

        BufferedImage bf = CopeImageUtil.cutHeadImages("Client/res/images/unKnownUser.png", avatarSize);
        avatarLabel = new JLabel(new ImageIcon(bf));
        avatarLabel.setFont(clientFont);
        avatarLabel.setBounds(126, 25, 48, 48);
        avatarLabel.setHorizontalAlignment(SwingConstants.CENTER);

        avatarPanel.add(avatarLabel);

        errLoginLabel = new ShadowLabel("");
        errLoginLabel.setFont(clientFont);
        errLoginLabel.setBounds(50, 75, 200, 18);
        errLoginLabel.setHorizontalAlignment(SwingConstants.CENTER);
        errLoginLabel.setForeground(Color.RED);
        errLoginLabel.setVisible(false);

        avatarPanel.add(errLoginLabel);

        // 用户id块

        gbc.gridy = 1;
        gbc.weighty = 1.0;

        uidPanel = new JPanel();
        uidPanel.setLayout(null);
        uidPanel.setOpaque(false);

        uidLabel = new JLabel("UID");
        uidLabel.setBounds(22, 10, 36, 15);
        uidLabel.setFont(blackStyleFont);
        uidLabel.setForeground(darkColor);

        uidPanel.add(uidLabel);

        uidField = new RoundedTextField();
        uidField.setHorizontalAlignment(SwingConstants.CENTER);
        uidField.setFont(textEditedStyleFont);
        uidField.setBounds(71, 10, 160, 20);
        uidField.setSelectionColor(Color.MAGENTA);
        uidField.setSelectedTextColor(Color.WHITE);
        uidField.addFocusListener(new HintFocusListener(uidField, "6位阿拉伯数字"));

        uidPanel.add(uidField);

        loginPanel.add(uidPanel, gbc);

        // 密码块

        gbc.gridy = 2;
        gbc.weighty = 1.0;

        passwordPanel = new JPanel();
        passwordPanel.setLayout(null);
        passwordPanel.setOpaque(false);

        passwordLabel = new JLabel("密码");
        passwordLabel.setBounds(20, 8, 24, 15);
        passwordLabel.setFont(blackStyleFont);
        passwordLabel.setForeground(darkColor);

        passwordPanel.add(passwordLabel);

        passwordField = new RoundedPasswordField();
        passwordField.setHorizontalAlignment(SwingConstants.CENTER);
        passwordField.setFont(textEditedStyleFont);
        passwordField.setBounds(71, 5, 160, 20);
        passwordField.setSelectionColor(Color.MAGENTA);
        passwordField.setSelectedTextColor(Color.WHITE);

        passwordPanel.add(passwordField);

        loginPanel.add(passwordPanel, gbc);

        // 选项按钮块

        gbc.gridy = 3;
        gbc.weighty = 1.0;

        optionPanel = new JPanel();
        optionPanel.setLayout(null);
        optionPanel.setOpaque(false);

        retrievePasswordBtn = new JButton("<html><u>忘记密码？点我找回</u></html>");
        retrievePasswordBtn.setFont(clientFont);
        retrievePasswordBtn.setForeground(Color.DARK_GRAY);
        retrievePasswordBtn.setBounds(5, 5, 95, 17);
        retrievePasswordBtn.addActionListener((e)->{
           cardLayout.show(gradientPanel, "retrievePassword");
           ClientLoginFrame.this.setTitle("RealChat-retrievePassword");
        });
        Dealer.setButtonTransparent(retrievePasswordBtn);
        Dealer.addHoverMouseListenerFor(retrievePasswordBtn);

        optionPanel.add(retrievePasswordBtn);

        confirmCheckinBtn = new JButton("登录");
        confirmCheckinBtn.setFont(clientFont);
        confirmCheckinBtn.setBounds(105, 5, 75, 20);
        confirmCheckinBtn.setFocusPainted(false);
        confirmCheckinBtn.addActionListener(this::onLoginButtonClicked);

        optionPanel.add(confirmCheckinBtn);

        jumpRegisterBtn = new JButton("<html><u>没有帐户？创建一个！</u></html>");
        jumpRegisterBtn.setFont(clientFont);
        jumpRegisterBtn.setForeground(Color.BLUE);
        jumpRegisterBtn.setBounds(190, 5, 100, 20);
        jumpRegisterBtn.addActionListener((e) -> {
            cardLayout.show(gradientPanel, "register");
            ClientLoginFrame.this.setTitle("RealChat-register");
        });
        Dealer.setButtonTransparent(jumpRegisterBtn);
        Dealer.addHoverMouseListenerFor(jumpRegisterBtn);

        optionPanel.add(jumpRegisterBtn);

        loginPanel.add(optionPanel, gbc);

        gradientPanel.add(loginPanel, "login");

        //---注册板

        registerPanel = new JPanel();
        registerPanel.setLayout(null);
        registerPanel.setOpaque(false);

        // UID由服务器自动分配
        /*  \头像上传区/
            |\|/|>|=|=|=|昵称
            |/|\|>|=|=|=|邮箱
            |>|>|=|=|=|=|密码
            |>|>|=|=|=|=|确认密码
            |>|>|=|=|=|=|验证码
            |<|O|(|=|=|)|按键及消息反馈

         */
        // 头像上传块

        avu = new AvatarUploader("Client/res/images/icons/toUpload.jpg");

        uploadAvatarLabel = avu.getUploadAvatarLabel();
        uploadAvatarLabel.setFont(clientFont);
        uploadAvatarLabel.setText("点此上传头像");
        uploadAvatarLabel.setBounds(10, 10, 60, 70);
        uploadAvatarLabel.setVerticalTextPosition(JLabel.BOTTOM);
        uploadAvatarLabel.setHorizontalTextPosition(JLabel.CENTER);
        Dealer.addHoverMouseListenerFor(uploadAvatarLabel);

        registerPanel.add(uploadAvatarLabel);

        // 昵称块

        nicknameLabel = new JLabel("昵称");
        nicknameLabel.setBounds(80, 10, 40, 20);
        nicknameLabel.setFont(blackStyleFont);
        nicknameLabel.setForeground(darkColor);

        registerPanel.add(nicknameLabel);

        nicknameField = new JTextField();
        nicknameField.setBounds(120, 10, 145, 20);
        nicknameField.setFont(textEditedStyleFont);
        nicknameField.setSelectionColor(Color.MAGENTA);
        nicknameField.setSelectedTextColor(Color.WHITE);

        registerPanel.add(nicknameField);

        // 邮箱块

        emailLabel = new JLabel("邮箱");
        emailLabel.setBounds(80, 35, 40, 20);
        emailLabel.setFont(blackStyleFont);
        emailLabel.setForeground(darkColor);

        registerPanel.add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(120, 35, 145, 20);
        emailField.setFont(textEditedStyleFont);
        emailField.setSelectionColor(Color.MAGENTA);
        emailField.setSelectedTextColor(Color.WHITE);
        emailField.addFocusListener(new HintFocusListener(emailField, "QQ,Google,Outlook等"));

        registerPanel.add(emailField);

        // 密码块

        newPasswordLabel = new JLabel("密码");
        newPasswordLabel.setBounds(15, 80, 60, 20);
        newPasswordLabel.setHorizontalAlignment(SwingConstants.CENTER);
        newPasswordLabel.setFont(blackStyleFont);
        newPasswordLabel.setForeground(darkColor);

        registerPanel.add(newPasswordLabel);

        newPasswordField = new JPasswordField();
        newPasswordField.setBounds(75, 80, 215, 20);
        newPasswordField.setFont(textEditedStyleFont);
        newPasswordField.setSelectionColor(Color.MAGENTA);
        newPasswordField.setSelectedTextColor(Color.WHITE);

        registerPanel.add(newPasswordField);

        // 确认密码块

        confirmPasswordLabel = new JLabel("确认密码");
        confirmPasswordLabel.setBounds(15, 105, 60, 20);
        confirmPasswordLabel.setHorizontalAlignment(SwingConstants.CENTER);
        confirmPasswordLabel.setFont(blackStyleFont);
        confirmPasswordLabel.setForeground(darkColor);

        registerPanel.add(confirmPasswordLabel);

        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setBounds(75, 105, 215, 20);
        confirmPasswordField.setFont(textEditedStyleFont);
        confirmPasswordField.setSelectionColor(Color.MAGENTA);
        confirmPasswordField.setSelectedTextColor(Color.WHITE);

        registerPanel.add(confirmPasswordField);

        // 验证码块

        verificationLabel = new JLabel("验证码");
        verificationLabel.setBounds(15, 130, 60, 20);
        verificationLabel.setHorizontalAlignment(SwingConstants.CENTER);
        verificationLabel.setFont(blackStyleFont);
        verificationLabel.setForeground(darkColor);

        registerPanel.add(verificationLabel);

        verificationField = new JTextField();
        verificationField.setBounds(80, 130, 80, 20);
        verificationField.setFont(textEditedStyleFont);
        verificationField.setSelectionColor(Color.MAGENTA);
        verificationField.setSelectedTextColor(Color.WHITE);
        verificationField.addFocusListener(new HintFocusListener(verificationField,
                "4位验证码"));

        registerPanel.add(verificationField);

        sendRequestBtn = new JButton("发送");
        sendRequestBtn.setFont(clientFont);
        sendRequestBtn.setBounds(180, 130, 60, 20);
        sendRequestBtn.setForeground(Color.CYAN);
        sendRequestBtn.setFocusPainted(false);
        sendRequestBtn.addActionListener(
            new CountDownActionListener(sendRequestBtn) {
                @Override
                public boolean checkProcess() {
                    boolean flag = false;
                    if (!new EMailChecker().checkValid(emailField.getText())) {
                        mtsReg.setErrorMessage("验证码需要有效邮箱!");
                    } else {
                        flag = true;
                    }
                    return flag;
                }
                @Override
                public void requestSendVC() {
                    CMsgProcessor.registerVC(emailField.getText());
                    mtsReg.setInfoMessage("验证码已发送");
                    mtsReg.transfer();
                }
            }
        );

        registerPanel.add(sendRequestBtn);

        // 帮助按钮

        helperButton = new JButton(new ImageIcon(helperIcon));
        helperButton.setBounds(260, 125, 30, 30);
        Dealer.setButtonTransparent(helperButton);
        Dealer.addHoverMouseListenerFor(helperButton);
        helperButton.addActionListener(e -> {
            cardLayout.show(gradientPanel, "helper");
            ClientLoginFrame.this.setTitle("RealChat-helper");
        });

        registerPanel.add(helperButton);

        // 按键及消息反馈块

        returnLoginBtn = new JButton("返回");
        returnLoginBtn.setFont(clientFont);
        returnLoginBtn.setBounds(15, 165, 50, 20);
        returnLoginBtn.addActionListener((e) -> {
            cardLayout.show(gradientPanel, "login");
            ClientLoginFrame.this.setTitle("RealChat-login");
        });

        registerPanel.add(returnLoginBtn);

        submitBtn = new JButton("提交");
        submitBtn.setFont(clientFont);
        submitBtn.setBounds(75, 165, 60, 20);
        submitBtn.addActionListener(this::onSubmitButtonClicked);

        registerPanel.add(submitBtn);



        feedbackField = new JTextField("无反馈");
        feedbackField.setBounds(145, 165, 145, 20);
        feedbackField.setFont(textEditedStyleFont);
        feedbackField.setEditable(false);
        feedbackField.setSelectionColor(Color.MAGENTA);
        feedbackField.setSelectedTextColor(Color.WHITE);
        mtsReg.bind(feedbackField);

        registerPanel.add(feedbackField);

        gradientPanel.add(registerPanel,"register");

        //---密码找回板

        retrievePasswordPanel = new JPanel();
        retrievePasswordPanel.setLayout(new GridLayout(4,1));
        retrievePasswordPanel.setOpaque(false);
        //(

        pastUidPanel = new JPanel();
        pastUidPanel.setLayout(null);
        pastUidPanel.setOpaque(false);
        retrievePasswordPanel.add(pastUidPanel);

        retVerificationPanel = new JPanel();
        retVerificationPanel.setLayout(null);
        retVerificationPanel.setOpaque(false);
        retrievePasswordPanel.add(retVerificationPanel);

        resetPasswordPanel = new JPanel();
        resetPasswordPanel.setLayout(null);
        resetPasswordPanel.setOpaque(false);
        retrievePasswordPanel.add(resetPasswordPanel);

        retRequestPanel = new JPanel();
        retRequestPanel.setLayout(null);
        retRequestPanel.setOpaque(false);
        retrievePasswordPanel.add(retRequestPanel);
        //)<

        pastUidLabel = new JLabel("UID");
        pastUidLabel.setFont(blackStyleFont);
        pastUidLabel.setBounds(20, 20, 60, 20);
        pastUidPanel.add(pastUidLabel);

        pastUidField = new JTextField();
        pastUidField.setFont(textEditedStyleFont);
        pastUidField.setBounds(80, 20, 120, 20);
        pastUidField.setSelectionColor(Color.MAGENTA);
        pastUidField.setSelectedTextColor(Color.WHITE);
        pastUidField.addFocusListener(new HintFocusListener(pastUidField, "6位阿拉伯数字"));
        pastUidPanel.add(pastUidField);
        //><

        retVerificationLabel = new JLabel("验证码");
        retVerificationLabel.setFont(blackStyleFont);
        retVerificationLabel.setBounds(20, 20, 70, 20);
        retVerificationPanel.add(retVerificationLabel);

        retVerificationField = new JTextField();
        retVerificationField.setFont(textEditedStyleFont);
        retVerificationField.setBounds(90, 20, 80, 20);
        retVerificationField.setSelectionColor(Color.MAGENTA);
        retVerificationField.setSelectedTextColor(Color.WHITE);
        retVerificationField.addFocusListener(new HintFocusListener(retVerificationField, "4位验证码"));
        retVerificationPanel.add(retVerificationField);

        retSendBtn = new JButton("发送");
        retSendBtn.setFont(blackStyleFont);
        retSendBtn.setBounds(210, 20, 70, 20);
        retSendBtn.setFocusPainted(false);
        retSendBtn.addActionListener(
            new CountDownActionListener(retSendBtn) {
                @Override
                public boolean checkProcess() {
                    boolean flag = false;
                    if (!new UIDChecker().checkValid(pastUidField.getText())) {
                        mtsRet.setErrorMessage("验证码需要合法UID!");
                    } else {
                        flag = true;
                    }
                    return flag;
                }
                @Override
                public void requestSendVC() {
                    CMsgProcessor.retrieveVC(pastUidField.getText());
                    mtsRet.setInfoMessage("验证码已发送");
                    mtsRet.transfer();
                }
        });
        retVerificationPanel.add(retSendBtn);
        //><

        resetPasswordLabel = new JLabel("新密码");
        resetPasswordLabel.setFont(blackStyleFont);
        resetPasswordLabel.setBounds(20, 20, 60, 20);
        resetPasswordPanel.add(resetPasswordLabel);

        resetPasswordField = new JPasswordField();
        resetPasswordField.setFont(textEditedStyleFont);
        resetPasswordField.setBounds(80, 20, 120, 20);
        resetPasswordField.setSelectionColor(Color.MAGENTA);
        resetPasswordField.setSelectedTextColor(Color.WHITE);
        resetPasswordPanel.add(resetPasswordField);
        //><

        retReturnBtn = new JButton("返回");
        retReturnBtn.setFont(clientFont);
        retReturnBtn.setBounds(20, 20, 60, 20);
        retReturnBtn.setFocusPainted(false);
        retReturnBtn.addActionListener((e -> {
            cardLayout.show(gradientPanel, "login");
            ClientLoginFrame.this.setTitle("RealChat-login");
        }));
        retRequestPanel.add(retReturnBtn);

        retRequestBtn = new JButton("找回");
        retRequestBtn.setFont(blackStyleFont);
        retRequestBtn.setBounds(110, 20, 80, 20);
        retRequestBtn.setFocusPainted(false);
        retRequestBtn.addActionListener(this::onRetrieveButtonClicked);
        retRequestPanel.add(retRequestBtn);

        retFeedbackField = new JTextField("无反馈");
        retFeedbackField.setBounds(200, 20, 90, 20);
        retFeedbackField.setFont(textEditedStyleFont);
        retFeedbackField.setEditable(false);
        retFeedbackField.setSelectionColor(Color.MAGENTA);
        retFeedbackField.setSelectedTextColor(Color.WHITE);
        mtsRet.bind(retFeedbackField);
        retRequestPanel.add(retFeedbackField);
        //>

        gradientPanel.add(retrievePasswordPanel, "retrievePassword");

        //---帮助界面

        helperPanel = new JPanel();
        helperPanel.setLayout(null);

        helperEditorPane = new JEditorPane();
        helperEditorPane.setFont(clientFont);
        helperEditorPane.setBounds(20, 20, 260, 140);
        helperEditorPane.setEditable(false);
        helperEditorPane.setContentType("text/html");
        helperEditorPane.setText("<html><h2>请注意!</h2>" +
                "<p>Q: 为什么尝试注册时报错？为什么发不了验证码给指定邮箱？" +
                "A: 请注意项目根目录下的无后缀文件名，配置一下config.ini文件。在你的邮箱网页版里开通SMTP服务，email填发送验证码的邮箱，"
                + "并复制<b>授权码</b>给auth_code的值才能正常启用发送验证码的服务！</p>" +
                "<p>Q: “我‘收不到’验证码？”<br>A: 有可能掉进<b>垃圾邮件箱</b>了，去翻一下 owo </p>" +
                "<h3>关于密码</h3>" +
                "<p>请设置长度为 6 ~ 20 位的仅含数字、字母、下划线字符的字符的串作为密码哦~</p>" +
                "</html>");
        helperPanel.add(helperEditorPane);

        helperReturnBtn = new JButton("返回");
        helperReturnBtn.setFont(clientFont);
        helperReturnBtn.setBounds(120, 170, 60, 20);
        helperReturnBtn.setFocusPainted(false);
        helperReturnBtn.addActionListener(e -> {
            cardLayout.show(gradientPanel, "register");
            ClientLoginFrame.this.setTitle("RealChat-register");
        });
        helperPanel.add(helperReturnBtn);

        gradientPanel.add(helperPanel, "helper");

        //===

        container.add(gradientPanel);

        setSize(300, 210);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

    }

    /**
     * 获取统一的临时通知弹窗的字体
     * @return 临时通知弹窗的字体
     */
    public Font getNotificationFont() {
        return notificationFont;
    }


    /**
     * 登录按钮点击事件
     * @param e 点击事件
     */
    private void onLoginButtonClicked(ActionEvent e) {
        if ((new UIDChecker()).checkValid(uidField.getText())) {
            char[] passwordArray = passwordField.getPassword();
            String password = new String(passwordArray);
            if ((new PasswordChecker(6, 20)).checkValid(password)) {
                ReboundTrigger rbt = new ReboundTrigger("请稍候");
                rbt.trig(confirmCheckinBtn);

                tryConnectByLogin(rbt);

                System.out.println("提交登录信息");
            }
            else {
                errLoginLabel.setErrText("密码不合规，请检查");
                (new DisappearTrigger()).trig(errLoginLabel);
            }
            Arrays.fill(passwordArray, '\0');
        }
        else {
            errLoginLabel.setErrText("uid不合规，请检查");
            (new DisappearTrigger()).trig(errLoginLabel);
        }
    }

    /**
     * 注册按钮点击事件
     * @param e 点击事件
     */
    private void onSubmitButtonClicked(ActionEvent e) {
        boolean pass = true;
        if (!((new NicknameChecker()).checkValid(nicknameField.getText()))) {
            mtsReg.setErrorMessage("昵称不合规");
            pass = false;
        }
        else if (!((new EMailChecker()).checkValid(emailField.getText()))) {
            mtsReg.setErrorMessage("无效的邮箱地址");
            pass = false;
        }
        else {
            char[] passwordArray = newPasswordField.getPassword();
            String password = new String(passwordArray);
            if (!((new PasswordChecker(6, 20)).checkValid(password))) {
                mtsReg.setErrorMessage("密码不合规");
                pass = false;
            }
            Arrays.fill(passwordArray, '\0');
            if (pass)
            {
                char[] confirmPasswordArray = confirmPasswordField.getPassword();
                String confirmPassword = new String(confirmPasswordArray);
                if (!password.equals(confirmPassword)) {
                    mtsReg.setErrorMessage("确认密码须与密码一致");
                    pass = false;
                }
                Arrays.fill(confirmPasswordArray, '\0');
            }
        }
        if (pass && !((new VerificationChecker()).checkValid(verificationField.getText()))) {
            mtsReg.setErrorMessage("验证码应是四位数字");
            pass = false;
        }
        mtsReg.transfer();

        if (pass) {
            (new ReboundTrigger("请稍等")).trig(submitBtn);
            CMsgProcessor.register(nicknameField.getText(), emailField.getText(), newPasswordField.getPassword(),
                    verificationField.getText(), ImageUtil.convertToBase64(avu.getCurrentAvatar()));
            mtsReg.setInfoMessage("等待服务器处理...");
            mtsReg.transfer();
        }

    }

    /**
     * 找回按钮点击事件
     * @param e 点击事件
     */
    private void onRetrieveButtonClicked(ActionEvent e) {
        boolean pass = true;
        if (!(new UIDChecker().checkValid(pastUidField.getText()))) {
            mtsRet.setErrorMessage("UID不合规");
            pass = false;
        }
        if (pass && (!((new VerificationChecker()).checkValid(retVerificationField.getText())))) {
            mtsRet.setErrorMessage("验证码须为四位数字");
            pass = false;
        }
        if (pass) {
            char[] passwordArray = resetPasswordField.getPassword();
            String password = new String(passwordArray);
            if (!((new PasswordChecker(6, 20)).checkValid(password))) {
                mtsRet.setErrorMessage("新密码设置不合规");
                pass = false;
            }
            Arrays.fill(passwordArray, '\0');
        }

        mtsRet.transfer();
        if (pass) {
            (new ReboundTrigger("请稍候")).trig(retRequestBtn);
            String retUid = pastUidField.getText();
            CMsgProcessor.retrieve(retUid, resetPasswordField.getPassword(),
                    retVerificationField.getText());
            ChatClient.setTempRetUid(retUid);
            mtsRet.setInfoMessage("等待服务器处理...");
            mtsRet.transfer();
        }
    }

    /**
     * 尝试连接到服务器，若正在连接则直接发送表单。
     * 已套 EDT
     */
    private void tryConnectByLogin(ReboundTrigger rbt) {
        new Thread(() -> {
            SwingUtilities.invokeLater(() -> {
                rbt.stopNow();
                confirmCheckinBtn.setEnabled(false);
            });
            // 检查与服务器的连接状态
            boolean isConnecting = chatClient.isConnecting();
            if (!isConnecting) {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setInfoText("正在连接...");
                });
                if (chatClient.reconnect("TRY_CONNECT_BY_LOGIN")) {
                    isConnecting = true;
                }
            }
            if (isConnecting) {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setPassText("连接成功，正在登录...");
                });
                // 从界面获取用户输入的UID和密码
                String uid = uidField.getText();
                char[] passwordArray = passwordField.getPassword();
                CMsgProcessor.login(uid, passwordArray);
                Arrays.fill(passwordArray, '\0');
            } else {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setErrText("连接失败，请稍后再试...");
                });
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {}
            SwingUtilities.invokeLater(() -> {
                confirmCheckinBtn.setEnabled(true);
            });
        }).start();
    }

    /**
     * 注册成功后，填充登录页的UID（分配到并收到的）,密码 和 头像，并转到
     */
    public void fillUIDPWDAndAvatar(String uid) {
        uidField.setText(uid);
        passwordField.setText(String.valueOf(newPasswordField.getPassword()));
        avatarLabel.setIcon(new ImageIcon(avu.getCurrentAvatar()));
        cardLayout.show(gradientPanel, "login");
        setTitle("RealChat-login");
    }

    /**
     * 登录失败的反馈显示。
     * 已套 EDT
     */
    public void loginErrFeedback(String feedback) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setErrText("登录失败，请重试");
            NotificationPopup.showNotification(feedback, notificationFont);
        });
    }

    /**
     * 注册失败的反馈显示。
     * 已套 EDT
     */
    public void registerErrFeedback(String feedback) {
        SwingUtilities.invokeLater(() -> {
            mtsReg.setErrorMessage("注册失败，请重试");
            NotificationPopup.showNotification(feedback, notificationFont);
        });
    }

    /**
     * 找回账户失败的反馈显示。
     * 已套 EDT
     */
    public void retrieveErrFeedback(String feedback) {
        SwingUtilities.invokeLater(() -> {
            mtsRet.setErrorMessage("找回账户失败！");
            NotificationPopup.showNotification(feedback, notificationFont);
        });
    }

    /**
     * 进入主界面。
     * 已套 EDT
     */
    public void enterMainFrame() {
        SwingUtilities.invokeLater(() -> {

            Timer timer = new Timer(2000, null);
            timer.setRepeats(false);
            timer.addActionListener(e -> {
                Dealer.adjustScreen(clientMainFrame);
                clientMainFrame.setLocationRelativeTo(null);
                clientMainFrame.setVisible(true);
                NotificationPopup.showNotification("登录成功", notificationFont);
                this.setVisible(false);
            });
            timer.start();
        });
    }

    /**
     * 用 nickname 改变欢迎语
     * 已套 EDT
     */
    public void changeWelcomeWords(String nickname) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setPassText("欢迎，" + nickname);
        });
    }

    /**
     * 改变头像为服务器发来的该 账号的 头像。
     * 已套 EDT
     */
    public void changeAvatarLabel() {
        SwingUtilities.invokeLater(() -> {
            BufferedImage bf = CopeImageUtil.cutHeadImages("Client/res/images/own/" + chatClient.getUid() +
                    ".png", avatarSize);
            if (bf != null) {
                avatarLabel.setIcon(new ImageIcon(bf));
            }
        });
    }

}
