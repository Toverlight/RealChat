package ClientSide;

import ClientSide.customized.CMsgProcessor;
import ClientSide.customized.Dealer;

import javax.swing.*;

public class ClientStart {
    public static void main(String[] args) {
        Dealer.adaptToOS();
        ChatClient chatClient = new ChatClient("127.0.0.1", 12345);
        CMsgProcessor.setChatClient(chatClient);
        SwingUtilities.invokeLater(() -> {
            ClientLoginFrame clientLoginFrame = new ClientLoginFrame("RealChat-Login", chatClient);
            CMsgProcessor.setClientLoginFrame(clientLoginFrame);
            Dealer.setImageIcon(clientLoginFrame, "Client/res/images/icons/bell.png");
            Dealer.adjustScreen(clientLoginFrame);
            Dealer.centerWindow(clientLoginFrame);
        });
        chatClient.connect();
    }
}
