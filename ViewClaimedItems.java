import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ViewClaimedItems extends JPanel {

    public ViewClaimedItems(String userEmail) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(240, 240, 240));

        String title = (userEmail != null) ? "YOUR CLAIMED ITEMS" : "USER'S CLAIMED ITEMS";
        JLabel header = new JLabel(title);
        header.setFont(new Font("SansSerif", Font.BOLD, 26));
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 0));
        headerPanel.add(header);
        add(headerPanel, BorderLayout.NORTH);

        String[] columns = { "Reference #", "Item Name", "Date Found", "Reported By", "Date Claimed", "Claimed By" };

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(35);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(25, 42, 86));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(230, 240, 255));
        table.setSelectionForeground(Color.BLACK);

        loadData(model, userEmail);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadData(DefaultTableModel model, String email) {
        model.setRowCount(0);
        ArrayList<String[]> items;

        if (email != null) {

            items = Database.getUserClaimedItems(email);
        } else {

            items = Database.getAllClaimedItems();
        }

        for (String[] item : items) {
            model.addRow(item);
        }
    }
}