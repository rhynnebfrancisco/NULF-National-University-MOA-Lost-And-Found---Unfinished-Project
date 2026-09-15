import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.*;

public class ViewPendingItemsPanel extends JPanel {

    public ViewPendingItemsPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(240, 240, 240));
        JLabel header = new JLabel("PENDING CLAIM REQUESTS");
        header.setFont(new Font("SansSerif", Font.BOLD, 26));
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 0));
        headerPanel.add(header);
        add(headerPanel, BorderLayout.NORTH);

        String[] columns = { "Item Name", "Landmark", "Date Found", "Time Found", "Asking for Claim", "Operation" };

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(40);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(0x3B468F));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(230, 240, 255));
        table.setSelectionForeground(Color.BLACK);

        table.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox()));
        table.getColumnModel().getColumn(5).setMinWidth(200);

        loadData(model);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadData(DefaultTableModel model) {
        model.setRowCount(0);
        try {

            ArrayList<String[]> items = Database.getPendingItemsWithClaimants();
            if (items != null) {
                for (String[] item : items) {
                    if (item != null && item.length >= 5) {
                        model.addRow(new Object[] {
                                item[0], item[1], item[2], item[3], item[4], ""
                        });
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading pending items: " + e.getMessage());
            e.printStackTrace();
        }
    }

    class ButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton viewBtn;
        private JButton msgBtn;

        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 2));
            setOpaque(true);
            setBackground(Color.WHITE);

            viewBtn = new JButton("View Item");
            viewBtn.setBackground(new Color(46, 204, 113));
            viewBtn.setForeground(Color.WHITE);
            viewBtn.setFocusPainted(false);
            viewBtn.setFont(new Font("SansSerif", Font.BOLD, 11));

            msgBtn = new JButton("Message");
            msgBtn.setBackground(new Color(52, 152, 219));
            msgBtn.setForeground(Color.WHITE);
            msgBtn.setFocusPainted(false);
            msgBtn.setFont(new Font("SansSerif", Font.BOLD, 11));

            add(viewBtn);
            add(msgBtn);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            if (isSelected)
                setBackground(table.getSelectionBackground());
            else
                setBackground(Color.WHITE);
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton viewBtn;
        private JButton msgBtn;
        private String itemName, landmark, date, time, claimer, desc, imgPath;
        private String claimerEmail;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 2));
            panel.setOpaque(true);
            panel.setBackground(Color.WHITE);

            viewBtn = new JButton("View Item");
            viewBtn.setBackground(new Color(46, 204, 113));
            viewBtn.setForeground(Color.WHITE);
            viewBtn.setFocusPainted(false);
            viewBtn.setFont(new Font("SansSerif", Font.BOLD, 11));

            msgBtn = new JButton("Message");
            msgBtn.setBackground(new Color(52, 152, 219));
            msgBtn.setForeground(Color.WHITE);
            msgBtn.setFocusPainted(false);
            msgBtn.setFont(new Font("SansSerif", Font.BOLD, 11));

            viewBtn.addActionListener(e -> {
                fireEditingStopped();
                showItemDetails();
            });

            msgBtn.addActionListener(e -> {
                fireEditingStopped();
                openMessage();
            });

            panel.add(viewBtn);
            panel.add(msgBtn);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                int column) {
            panel.setBackground(table.getSelectionBackground());
            ArrayList<String[]> allItems = Database.getPendingItemsWithClaimants();
            if (allItems != null && row < allItems.size()) {
                String[] itemData = allItems.get(row);
                itemName = itemData[0];
                landmark = itemData[1];
                date = itemData[2];
                time = itemData[3];
                claimer = itemData[4];
                desc = itemData[5];
                imgPath = itemData[6];

                if (itemData.length > 7) {
                    claimerEmail = itemData[7];
                } else {
                    claimerEmail = claimer;
                }
            }
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }

        private void openMessage() {

            Container parent = getParent();
            while (parent != null) {
                if (parent instanceof HomePage) {
                    ((HomePage) parent).openMessaging(claimerEmail);
                    return;
                } else if (parent instanceof AdminDashboard) {

                    ((AdminDashboard) parent).openMessaging(claimerEmail);
                    return;
                }
                parent = parent.getParent();
            }

            JOptionPane.showMessageDialog(panel, "Opening chat with " + claimerEmail);
        }

        private void showItemDetails() {
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this.panel), "Pending Claim Details",
                    true);
            dialog.setSize(500, 600);
            dialog.setLocationRelativeTo(null);
            dialog.setLayout(null);
            dialog.getContentPane().setBackground(Color.WHITE);

            JLabel title = new JLabel("ITEM DETAILS");
            title.setFont(new Font("SansSerif", Font.BOLD, 22));
            title.setForeground(new Color(0x3B468F));
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
            addDetail(dialog, "Claimant:", claimer, y + 30);
            addDetail(dialog, "Date/Time:", date + " " + time, y + 60);

            JLabel descLbl = new JLabel("Description:");
            descLbl.setFont(new Font("SansSerif", Font.BOLD, 14));
            descLbl.setBounds(50, y + 90, 100, 20);
            dialog.add(descLbl);

            JTextArea descArea = new JTextArea(desc);
            descArea.setWrapStyleWord(true);
            descArea.setLineWrap(true);
            descArea.setEditable(false);
            descArea.setBackground(new Color(245, 248, 250));
            JScrollPane scroll = new JScrollPane(descArea);
            scroll.setBounds(150, y + 90, 300, 60);
            dialog.add(scroll);

            JButton closeBtn = new JButton("Close");
            closeBtn.setBounds(200, 500, 100, 40);
            closeBtn.setBackground(new Color(0x3B468F));
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