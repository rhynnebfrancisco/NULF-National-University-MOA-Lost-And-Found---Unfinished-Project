import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.table.*;

public class ViewLostItemsPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private String currentUserEmail;

    public ViewLostItemsPanel(String userEmail) {
        this.currentUserEmail = userEmail;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(240, 240, 240));
        JLabel header = new JLabel("VIEW LOST ITEM");
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
        table.getTableHeader().setBackground(new Color(0x3B468F));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(230, 240, 255));
        table.setSelectionForeground(Color.BLACK);

        table.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(scrollPane, BorderLayout.CENTER);

        loadItemsData();

        addAncestorListener(new AncestorListener() {
            public void ancestorAdded(AncestorEvent event) {
                loadItemsData();
            }

            public void ancestorRemoved(AncestorEvent event) {
            }

            public void ancestorMoved(AncestorEvent event) {
            }
        });
    }

    public void loadItemsData() {
        model.setRowCount(0);

        ArrayList<String[]> allItems = Database.getUnclaimedItems();

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

        for (String[] item : allItems) {

            if (!restrictedItemNames.contains(item[1])) {
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
            setText("View");
            setBackground(new Color(46, 204, 113));
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setFont(new Font("SansSerif", Font.BOLD, 12));
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            if (isSelected) {
                setBackground(new Color(40, 180, 99));
            } else {
                setBackground(new Color(46, 204, 113));
            }
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String itemId, itemName, landmark, date, time, reporter, desc, imgPath;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("View");
            button.setOpaque(true);
            button.setBackground(new Color(46, 204, 113));
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setFont(new Font("SansSerif", Font.BOLD, 12));

            button.addActionListener(e -> {
                fireEditingStopped();
                showItemDetails();
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                int column) {

            ArrayList<String[]> allItems = Database.getUnclaimedItems();

            ArrayList<String[]> pendingClaims = Database.getPendingClaims();
            ArrayList<String> restrictedItemNames = new ArrayList<>();
            if (pendingClaims != null) {
                for (String[] c : pendingClaims)
                    restrictedItemNames.add(c[1]);
            }

            ArrayList<String[]> approvedClaims = Database.getAllClaimedItems();
            if (approvedClaims != null) {
                for (String[] c : approvedClaims)
                    restrictedItemNames.add(c[1]);
            }

            ArrayList<String[]> filteredItems = new ArrayList<>();
            for (String[] item : allItems) {
                if (!restrictedItemNames.contains(item[1])) {
                    filteredItems.add(item);
                }
            }

            if (row < filteredItems.size()) {
                String[] itemData = filteredItems.get(row);
                itemId = itemData[0];
                itemName = itemData[1];
                landmark = itemData[2];
                date = itemData[3];
                time = itemData[4];
                reporter = itemData[5];
                desc = itemData[6];
                imgPath = itemData[7];
            }
            return button;
        }

        public Object getCellEditorValue() {
            return "View";
        }

        private void showItemDetails() {
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this.button), "Item Details", true);
            dialog.setSize(500, 650);
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
            addDetail(dialog, "Landmark:", landmark, y + 30);
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

            JButton claimBtn = new JButton("Request Claim");
            claimBtn.setBounds(50, 530, 180, 45);
            claimBtn.setBackground(new Color(0x3B468F));
            claimBtn.setForeground(Color.WHITE);
            claimBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
            claimBtn.setFocusPainted(false);
            claimBtn.addActionListener(e -> {
                requestClaim(dialog);
            });
            dialog.add(claimBtn);

            JButton closeBtn = new JButton("Close");
            closeBtn.setBounds(260, 530, 180, 45);
            closeBtn.setBackground(new Color(231, 76, 60));
            closeBtn.setForeground(Color.WHITE);
            closeBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
            closeBtn.setFocusPainted(false);
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

        private void requestClaim(JDialog parentDialog) {
            if (reporter.equals(currentUserEmail)) {
                JOptionPane.showMessageDialog(parentDialog,
                        "You cannot request a claim for your own reported item.",
                        "Action Not Allowed",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Check if item is already claimed
            if (Database.isItemAlreadyClaimed(Integer.parseInt(itemId))) {
                JOptionPane.showMessageDialog(parentDialog,
                        "This item has already been claimed and cannot be requested again.",
                        "Item Unavailable",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String proof = JOptionPane.showInputDialog(parentDialog,
                    "Please provide specific details to prove ownership (e.g., wallpapers, scratches, unique marks):",
                    "Proof of Ownership",
                    JOptionPane.QUESTION_MESSAGE);

            if (proof != null && !proof.trim().isEmpty()) {
                boolean success = Database.submitClaim(Integer.parseInt(itemId), currentUserEmail, proof.trim());
                if (success) {
                    JOptionPane.showMessageDialog(parentDialog,
                            "Claim request submitted successfully! Waiting for Admin approval.");
                    parentDialog.dispose();
                    loadItemsData();
                } else {
                    JOptionPane.showMessageDialog(parentDialog,
                            "Failed to submit claim. You may have already claimed this item.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }

    }
}