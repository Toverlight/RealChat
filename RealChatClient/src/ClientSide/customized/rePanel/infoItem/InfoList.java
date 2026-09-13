package ClientSide.customized.rePanel.infoItem;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class InfoList extends JPanel {
    private final List<InfoItem> items = new ArrayList<>();
    private InfoItem selectedItem = null;

    public InfoList() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
    }

    public void addItem(InfoItem item) {
        items.add(item);
        add(item);
        revalidate();
        repaint();
    }

    public void setSelectedItem(InfoItem item) {
        if (selectedItem != null) {
            selectedItem.setSelected(false);
        }
        selectedItem = item;
        if (selectedItem != null) {
            selectedItem.setSelected(true);
        }
    }

    public InfoItem getSelectedItem() {
        return selectedItem;
    }
}

