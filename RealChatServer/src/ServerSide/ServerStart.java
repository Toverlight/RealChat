package ServerSide;

import ServerSide.customized.Dealer;
import ServerSide.customized.SMsgProcessor;

import javax.swing.*;

public class ServerStart {
    public static void main(String[] args) {
        Dealer.adaptToOS();
        ChatServer chatServer = new ChatServer(12345);
        SMsgProcessor.setChatServer(chatServer);
        SwingUtilities.invokeLater(() -> {
            ServerManagementFrame serverManagementFrame = new ServerManagementFrame("RealChat Server", chatServer);
            SMsgProcessor.setServerManagementFrame(serverManagementFrame);
            Dealer.setImageIcon(serverManagementFrame, "Server/res/imgs/serverIcon.png");
            Dealer.adjustScreen(serverManagementFrame);
            Dealer.centerWindow(serverManagementFrame);
        });
        chatServer.startService();
    }
}
