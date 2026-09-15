import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.*;

public class AdminPendingAccounts extends JPanel {
    private JTable table;
    private DefaultTableModel model;

    public AdminPendingAccounts() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Header panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(240, 240, 240));
        JLabel header = new JLabel("PENDING ACCOUNT REQUESTS");
        header.setFont(new Font("SansSerif", Font.BOLD, 26));
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 0));
        headerPanel.add(header);
        add(headerPanel, BorderLayout.NORTH);

        // Table columns
        String[] columns = { "Student No.", "Full Name", "Email", "Department", "Date Applied", "Action" };

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // only action column editable
            }
        };

        table = new JTable(model);
        table.setRowHeight(50);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(25, 42, 86));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(230, 240, 255));
        table.setSelectionForeground(Color.BLACK);

        table.getColumnModel().getColumn(5).setMinWidth(280);
        table.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox(), this));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        // Load pending data initially
        loadPendingData();

        // ✅ Add listener para mag-reload everytime binuksan ang panel
        addAncestorListener(new javax.swing.event.AncestorListener() {
            @Override
            public void ancestorAdded(javax.swing.event.AncestorEvent event) {
                loadPendingData(); // reload latest pending users
            }

            @Override
            public void ancestorRemoved(javax.swing.event.AncestorEvent event) {}
            @Override
            public void ancestorMoved(javax.swing.event.AncestorEvent event) {}
        });
    }

    // Load pending users from database
    public void loadPendingData() {
        model.setRowCount(0);
        ArrayList<String[]> users = Database.getPendingUsers();
        for (String[] user : users) {
            model.addRow(new Object[] {
                    user[0],   // student ID
                    user[1],   // full name
                    user[2],   // email
                    user[3],   // department/program
                    user[4],   // date applied
                    ""         // action buttons
            });
        }
    }

    // Table button renderer
    class ButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton approveBtn;
        private JButton rejectBtn;

        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 10, 8));
            setOpaque(true);
            setBackground(Color.WHITE);

            approveBtn = new JButton("Approve");
            approveBtn.setBackground(new Color(46, 204, 113));
            approveBtn.setForeground(Color.WHITE);
            approveBtn.setFocusPainted(false);
            approveBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
            approveBtn.setPreferredSize(new Dimension(90, 34));

            rejectBtn = new JButton("Reject");
            rejectBtn.setBackground(new Color(231, 76, 60));
            rejectBtn.setForeground(Color.WHITE);
            rejectBtn.setFocusPainted(false);
            rejectBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
            rejectBtn.setPreferredSize(new Dimension(90, 34));

            add(approveBtn);
            add(rejectBtn);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(Color.WHITE);
            }
            return this;
        }
    }

    // Table button editor
    class ButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton approveBtn;
        private JButton rejectBtn;
        private String currentName;
        private String currentEmail;
        private AdminPendingAccounts parentPanel;

        public ButtonEditor(JCheckBox checkBox, AdminPendingAccounts parent) {
            super(checkBox);
            this.parentPanel = parent;
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
            panel.setOpaque(true);
            panel.setBackground(Color.WHITE);

            approveBtn = new JButton("Approve");
            approveBtn.setBackground(new Color(46, 204, 113));
            approveBtn.setForeground(Color.WHITE);
            approveBtn.setFocusPainted(false);
            approveBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
            approveBtn.setPreferredSize(new Dimension(90, 34));

            rejectBtn = new JButton("Reject");
            rejectBtn.setBackground(new Color(231, 76, 60));
            rejectBtn.setForeground(Color.WHITE);
            rejectBtn.setFocusPainted(false);
            rejectBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
            rejectBtn.setPreferredSize(new Dimension(90, 34));

            approveBtn.addActionListener(e -> {
                fireEditingStopped();
                approveUser();
            });

            rejectBtn.addActionListener(e -> {
                fireEditingStopped();
                rejectUser();
            });

            panel.add(approveBtn);
            panel.add(rejectBtn);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                int column) {
            currentName = (String) table.getValueAt(row, 1);
            currentEmail = (String) table.getValueAt(row, 2);
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }

        private void approveUser() {
            int confirm = JOptionPane.showConfirmDialog(panel,
                    "Approve account for " + currentName + "?",
                    "Confirm Approval",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = Database.approveUser(currentEmail);
                if (success) {
                    JOptionPane.showMessageDialog(panel, "Account Approved.");
                    parentPanel.loadPendingData();
                } else {
                    JOptionPane.showMessageDialog(panel, "Error approving user.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        private void rejectUser() {
            int confirm = JOptionPane.showConfirmDialog(panel,
                    "Reject and delete application for " + currentName + "?",
                    "Confirm Rejection",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = Database.rejectUser(currentEmail);
                if (success) {
                    JOptionPane.showMessageDialog(panel, "Application Rejected and Deleted.");
                    parentPanel.loadPendingData();
                } else {
                    JOptionPane.showMessageDialog(panel, "Error rejecting user.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
