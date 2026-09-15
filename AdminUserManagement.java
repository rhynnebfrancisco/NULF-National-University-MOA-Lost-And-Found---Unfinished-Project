import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.table.*;

public class AdminUserManagement extends JPanel {
    private JTable table;
    private DefaultTableModel model;

    private JPanel parentContentArea;
    private CardLayout parentLayout;

    public AdminUserManagement() {
        this(null, null);
    }

    public AdminUserManagement(JPanel contentArea, CardLayout layout) {
        this.parentContentArea = contentArea;
        this.parentLayout = layout;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(240, 240, 240));
        JLabel header = new JLabel("MANAGE USERS ACCOUNT");
        header.setFont(new Font("SansSerif", Font.BOLD, 26));
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 0));
        headerPanel.add(header);
        add(headerPanel, BorderLayout.NORTH);

        String[] columns = { "Student No.", "Full Name", "Email", "Role", "Department", "User Level", "Action" };

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6;
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

        table.getColumnModel().getColumn(6).setMinWidth(280);
        table.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(new JCheckBox(), this));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(scrollPane, BorderLayout.CENTER);

        addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                loadUserData();
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {
            }

            @Override
            public void ancestorMoved(AncestorEvent event) {
            }
        });

        loadUserData();
    }

    public void setParentComponents(JPanel contentArea, CardLayout layout) {
        this.parentContentArea = contentArea;
        this.parentLayout = layout;
    }

    public JPanel getParentContentArea() {
        return parentContentArea;
    }

    public CardLayout getParentLayout() {
        return parentLayout;
    }

    public void loadUserData() {
        if (model == null)
            return;
        model.setRowCount(0);

        ArrayList<String[]> users = Database.getAllUsers();
        for (String[] user : users) {

            model.addRow(new Object[] {
                    user[0], user[1], user[2], user[5], user[4], "N/A", ""
            });
        }
    }

    class ButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton updateBtn;
        private JButton deactivateBtn;

        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 10, 8));
            setOpaque(true);
            setBackground(Color.WHITE);

            updateBtn = new JButton("Update");
            updateBtn.setBackground(new Color(46, 204, 113));
            updateBtn.setForeground(Color.WHITE);
            updateBtn.setFocusPainted(false);
            updateBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
            updateBtn.setPreferredSize(new Dimension(80, 34));

            deactivateBtn = new JButton("Deactivate");
            deactivateBtn.setBackground(new Color(231, 76, 60));
            deactivateBtn.setForeground(Color.WHITE);
            deactivateBtn.setFocusPainted(false);
            deactivateBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
            deactivateBtn.setPreferredSize(new Dimension(100, 34));

            add(updateBtn);
            add(deactivateBtn);
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

    class ButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton updateBtn;
        private JButton deactivateBtn;
        private String currentEmail;
        private String currentName;
        private String currentId;
        private String currentProgram;
        private String currentRole;
        private AdminUserManagement parentPanel;

        public ButtonEditor(JCheckBox checkBox, AdminUserManagement parent) {
            super(checkBox);
            this.parentPanel = parent;
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
            panel.setOpaque(true);
            panel.setBackground(Color.WHITE);

            updateBtn = new JButton("Update");
            updateBtn.setBackground(new Color(46, 204, 113));
            updateBtn.setForeground(Color.WHITE);
            updateBtn.setFocusPainted(false);
            updateBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
            updateBtn.setPreferredSize(new Dimension(80, 34));

            deactivateBtn = new JButton("Deactivate");
            deactivateBtn.setBackground(new Color(231, 76, 60));
            deactivateBtn.setForeground(Color.WHITE);
            deactivateBtn.setFocusPainted(false);
            deactivateBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
            deactivateBtn.setPreferredSize(new Dimension(100, 34));

            updateBtn.addActionListener(e -> {
                fireEditingStopped();
                switchToUpdatePage();
            });

            deactivateBtn.addActionListener(e -> {
                fireEditingStopped();
                deactivateUser();
            });

            panel.add(updateBtn);
            panel.add(deactivateBtn);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                int column) {
            currentId = (String) table.getValueAt(row, 0);
            currentName = (String) table.getValueAt(row, 1);
            currentEmail = (String) table.getValueAt(row, 2);
            currentRole = (String) table.getValueAt(row, 3);
            currentProgram = (String) table.getValueAt(row, 4);

            panel.setBackground(table.getSelectionBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }

        private void switchToUpdatePage() {
            if (parentPanel.getParentContentArea() == null || parentPanel.getParentLayout() == null) {
                JOptionPane.showMessageDialog(panel, "Error: Navigation not set up properly.");
                return;
            }

            AdminUpdateUser updatePanel = new AdminUpdateUser(
                    currentName, currentId, currentEmail, currentProgram, "2nd Year", "N/A",
                    parentPanel.getParentContentArea(), parentPanel.getParentLayout(), parentPanel);

            parentPanel.getParentContentArea().add(updatePanel, "UPDATE_USER_PANEL");
            parentPanel.getParentLayout().show(parentPanel.getParentContentArea(), "UPDATE_USER_PANEL");
        }

        private void deactivateUser() {
            int confirm = JOptionPane.showConfirmDialog(panel,
                    "Are you sure you want to deactivate/delete " + currentName + "?\nThis action cannot be undone.",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = Database.deleteUser(currentEmail);
                if (success) {
                    JOptionPane.showMessageDialog(panel, "Account " + currentName + " deleted successfully.");
                    parentPanel.loadUserData();
                } else {
                    JOptionPane.showMessageDialog(panel, "Error deleting user.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}