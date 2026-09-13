package ClientSide.customized.rePanel;

import javax.swing.*;
import java.awt.*;

public class DynamicPanel extends JPanel {
    private Image backgroundImage;

    public DynamicPanel(Image img){
        backgroundImage = img;
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);

        g.drawImage(backgroundImage, 0, 0, this);
    }
}
