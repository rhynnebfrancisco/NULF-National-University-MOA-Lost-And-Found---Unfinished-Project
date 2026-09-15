import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.table.*;

public class AdminUnclaimedItems extends JPanel {
    private JTable table;
    private DefaultTableModel model;

    private ArrayList<String[]> allItemsData;

    public AdminUnclaimedItems() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(240, 240, 240));
        JLabel header = new JLabel("UNCLAIMED ITEMS");
        header.setFont(new Font("SansSerif", Font.BOLD, 26));
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 0));
        headerPanel.add(header);
        add(headerPanel, BorderLayout.NORTH);

        String[] columns = { "Item Name", "Landmark", "Date Found", "Time Found", "Reported By", "Action" };

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };

        table = new JTable(model);
        table.setRowHeight(40);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(25, 42, 86));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(230, 240, 255));
        table.setSelectionForeground(Color.BLACK);

        table.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox()));
        table.getColumnModel().getColumn(5).setMinWidth(150);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(scrollPane, BorderLayout.CENTER);

        loadData();

        addAncestorListener(new AncestorListener() {
            public void ancestorAdded(AncestorEvent event) {
                loadData();
            }

            public void ancestorRemoved(AncestorEvent event) {
            }

            public void ancestorMoved(AncestorEvent event) {
            }
        });
    }

    private void loadData() {
        model.setRowCount(0);
        ArrayList<String[]> rawItems = Database.getUnclaimedItems();
        allItemsData = new ArrayList<>();

        ArrayList<String[]> pendingClaims = Database.getPendingClaims();
        ArrayList<String> restrictedItemNames = new ArrayList<>();
        if (pendingClaims != null) {
            for (String[] c : pendingClaims) {
                restrictedItemNames.add(c[1]);
            }
        }

        ArrayList<String[]> approvedClaims = Database.getAllClaimedItems();
        if (approvedClaims != null) {
            for (String[] c : approvedClaims) {
                restrictedItemNames.add(c[1]);
            }
        }

        for (String[] item : rawItems) {

            if (!restrictedItemNames.contains(item[1])) {
                allItemsData.add(item);
                model.addRow(new Object[] {
                        item[1],
                        item[2],
                        item[3],
                        item[4],
                        item[5],
                        ""
                });
            }
        }
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setText("Details");
            setBackground(new Color(52, 152, 219));
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setFont(new Font("SansSerif", Font.BOLD, 12));
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            if (isSelected)
                setBackground(new Color(41, 128, 185));
            else
                setBackground(new Color(52, 152, 219));
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String itemName, landmark, date, time, reporter, desc, imgPath;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("Details");
            button.setOpaque(true);
            button.setBackground(new Color(52, 152, 219));
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setFont(new Font("SansSerif", Font.BOLD, 12));

            button.addActionListener(e -> {
                fireEditingStopped();
                showDetailsDialog();
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                int column) {

            if (row < allItemsData.size()) {
                String[] data = allItemsData.get(row);

                itemName = data[1];
                landmark = data[2];
                date = data[3];
                time = data[4];
                reporter = data[5];
                desc = data[6];
                imgPath = data[7];
            }
            return button;
        }

        public Object getCellEditorValue() {
            return "Details";
        }

        private void showDetailsDialog() {
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this.button), "Item Details", true);
            dialog.setSize(500, 600);
            dialog.setLocationRelativeTo(null);
            dialog.setLayout(null);
            dialog.getContentPane().setBackground(Color.WHITE);

            JLabel title = new JLabel("ITEM DETAILS");
            title.setFont(new Font("SansSerif", Font.BOLD, 22));
            title.setForeground(new Color(25, 42, 86));
            title.setBounds(150, 20, 200, 30);
            dialog.add(title);

            JLabel imgLabel = new JLabel();
            imgLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            imgLabel.setBounds(100, 70, 300, 200);
            imgLabel.setHorizontalAlignment(JLabel.CENTER);
            if (imgPath != null && !imgPath.isEmpty()) {
                ImageIcon icon = new ImageIcon(imgPath);
                Image img = icon.getImage().getScaledInstance(300, 200, Image.SCALE_SMOOTH);
                imgLabel.setIcon(new ImageIcon(img));
            } else {
                imgLabel.setText("No Image Available");
            }
            dialog.add(imgLabel);

            int y = 300;
            addDetail(dialog, "Item Name:", itemName, y);
            addDetail(dialog, "Found At:", landmark, y + 30);
            addDetail(dialog, "Date/Time:", date + " " + time, y + 60);
            addDetail(dialog, "Found By:", reporter, y + 90);

            JLabel descLbl = new JLabel("Description:");
            descLbl.setFont(new Font("SansSerif", Font.BOLD, 14));
            descLbl.setBounds(50, y + 120, 100, 20);
            dialog.add(descLbl);

            JTextArea descArea = new JTextArea(desc);
            descArea.setWrapStyleWord(true);
            descArea.setLineWrap(true);
            descArea.setEditable(false);
            descArea.setBackground(new Color(245, 248, 250));
            JScrollPane scroll = new JScrollPane(descArea);
            scroll.setBounds(150, y + 120, 300, 60);
            dialog.add(scroll);

            JButton closeBtn = new JButton("Close");
            closeBtn.setBounds(200, 510, 100, 40);
            closeBtn.setBackground(new Color(25, 42, 86));
            closeBtn.setForeground(Color.WHITE);
            closeBtn.addActionListener(e -> dialog.dispose());
            dialog.add(closeBtn);

            dialog.setVisible(true);
        }

        private void addDetail(JDialog d, String label, String value, int y) {
            JLabel l = new JLabel(label);
            l.setFont(new Font("SansSerif", Font.BOLD, 14));
            l.setBounds(50, y, 100, 20);
            d.add(l);
            JLabel v = new JLabel(value);
            v.setFont(new Font("SansSerif", Font.PLAIN, 14));
            v.setBounds(150, y, 300, 20);
            d.add(v);
        }
    }
}