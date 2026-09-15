import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.table.DefaultTableModel;

public class AdminClaimedHistory extends JPanel {

    private DefaultTableModel model;

    public AdminClaimedHistory() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(240, 240, 240));
        JLabel header = new JLabel("HISTORY OF CLAIMED ITEMS");
        header.setFont(new Font("SansSerif", Font.BOLD, 26));
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 0));
        headerPanel.add(header);
        add(headerPanel, BorderLayout.NORTH);

        String[] columns = { "Reference #", "Item Name", "Date Found", "Reported By", "Date Claimed", "Claimed By" };

        model = new DefaultTableModel(columns, 0) {
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

        // Initial load
        loadData();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(scrollPane, BorderLayout.CENTER);
        
        // Add Listener to refresh when shown
        addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                loadData();
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {
            }

            @Override
            public void ancestorMoved(AncestorEvent event) {
            }
        });
    }

    private void loadData() {
        if (model == null) return;
        model.setRowCount(0);
        // Ensure Database fetch is fresh
        ArrayList<String[]> items = Database.getAllClaimedItems();
        if (items != null) {
            for (String[] item : items) {
                model.addRow(item);
            }
        }
    }
}