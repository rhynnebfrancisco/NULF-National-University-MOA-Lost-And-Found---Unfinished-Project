import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashSet;
import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.table.DefaultTableModel;

public class HomePage extends JFrame {

    private String currentUserEmail;
    private JPanel sidebar;
    private boolean sidebarVisible = true;
    private JPanel mainContentPanel;
    private JPanel dashboardPanel;
    private MessagingPanel messagingPanel;
    private JLabel myClaimCountLabel;
    private JLabel allClaimedCountLabel;
    private JLabel pendingCountLabel;
    private DefaultTableModel recentItemsModel;

    public HomePage(String email) {
        this.currentUserEmail = email;

        String[] details = Database.getUserDetails(email);
        String userFullName = (details != null) ? details[0] + " " + details[1] : "USER";

        setTitle("NULF - Students");
        setSize(1150, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        ImageIcon logoIcon = new ImageIcon(getClass().getResource("Logo-NULF.png"));
        setIconImage(logoIcon.getImage());

        sidebar = new JPanel();
        sidebar.setBackground(new Color(25, 42, 86));
        sidebar.setPreferredSize(new Dimension(260, getHeight()));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JPanel menuHeader = new JPanel(new FlowLayout(FlowLayout.LEFT));
        menuHeader.setBackground(new Color(25, 42, 86));
        menuHeader.setMaximumSize(new Dimension(260, 55));

        JButton hamburgerBtn = new JButton("☰");
        hamburgerBtn.setFont(new Font("SansSerif", Font.BOLD, 20));
        hamburgerBtn.setForeground(Color.WHITE);
        hamburgerBtn.setBackground(new Color(25, 42, 86));
        hamburgerBtn.setFocusPainted(false);
        hamburgerBtn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        hamburgerBtn.addActionListener(this::toggleSidebar);

        JLabel menuLabel = new JLabel("MENU");
        menuLabel.setForeground(Color.WHITE);
        menuLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

        menuHeader.add(hamburgerBtn);
        menuHeader.add(menuLabel);

        sidebar.add(menuHeader);
        sidebar.add(Box.createVerticalStrut(6));

        sidebar.add(createUserInfoBox("👤", userFullName.toUpperCase(), "Student"));

        sidebar.add(createNavBox("📊", "DASHBOARD", "DASHBOARD"));
        sidebar.add(createNavBox("🔎", "VIEW LOST ITEM", "VIEW_ITEM"));
        sidebar.add(createNavBox("📝", "REPORT MISSING ITEMS", "REPORT_ITEM"));
        sidebar.add(createNavBox("💬", "MESSAGING", "MESSAGING"));
        sidebar.add(createNavBox("✅", "CLAIM CONFIRMATION", "CLAIM_CONFIRM"));
        sidebar.add(createNavBox("❓", "FAQ'S", "FAQS"));

        sidebar.add(Box.createVerticalGlue());
        sidebar.add(createNavBox("🚪", "LOGOUT", "LOGOUT"));
        sidebar.add(Box.createVerticalStrut(20));

        dashboardPanel = createDashboardUI();

        messagingPanel = new MessagingPanel(currentUserEmail);

        mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.add(dashboardPanel, BorderLayout.CENTER);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(sidebar, BorderLayout.WEST);
        getContentPane().add(mainContentPanel, BorderLayout.CENTER);
    }

    private JPanel createDashboardUI() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(240, 240, 240));
        JLabel header = new JLabel("USER DASHBOARD");
        header.setFont(new Font("SansSerif", Font.BOLD, 26));
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 0));
        headerPanel.add(header);
        panel.add(headerPanel, BorderLayout.NORTH);

        JPanel mainContent = new JPanel(new BorderLayout(0, 30));
        mainContent.setBackground(Color.WHITE);
        mainContent.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel summaryGrid = new JPanel(new GridLayout(1, 3, 20, 0));
        summaryGrid.setBackground(Color.WHITE);

        myClaimCountLabel = new JLabel("...", SwingConstants.LEFT);
        allClaimedCountLabel = new JLabel("...", SwingConstants.LEFT);
        pendingCountLabel = new JLabel("...", SwingConstants.LEFT);

        styleCountLabel(myClaimCountLabel);
        styleCountLabel(allClaimedCountLabel);
        styleCountLabel(pendingCountLabel);

        summaryGrid.add(createSummaryBox("Your Claimed items", myClaimCountLabel, "MY_CLAIMED"));

        summaryGrid.add(createSummaryBox("User's Claimed items", allClaimedCountLabel, "ALL_CLAIMED"));

        summaryGrid.add(createSummaryBox("View Pending items", pendingCountLabel, "VIEW_PENDING"));

        JPanel summaryWrapper = new JPanel(new BorderLayout());
        summaryWrapper.setBackground(Color.WHITE);
        summaryWrapper.add(summaryGrid, BorderLayout.CENTER);
        summaryWrapper.setPreferredSize(new Dimension(0, 180));

        mainContent.add(summaryWrapper, BorderLayout.NORTH);

        JPanel tablePanel = new JPanel(new BorderLayout(0, 10));
        tablePanel.setBackground(Color.WHITE);

        JLabel tableLabel = new JLabel("Displaying most recent lost item");
        tableLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        tableLabel.setForeground(new Color(25, 42, 86));
        tablePanel.add(tableLabel, BorderLayout.NORTH);

        String[] columns = { "Item Name", "Landmark", "Date Found", "Time Found" };
        recentItemsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(recentItemsModel);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(25, 42, 86));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(230, 240, 255));
        table.setSelectionForeground(Color.BLACK);

        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        mainContent.add(tablePanel, BorderLayout.CENTER);

        panel.add(mainContent, BorderLayout.CENTER);

        refreshDashboardData();

        panel.addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                refreshDashboardData();
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {
            }

            @Override
            public void ancestorMoved(AncestorEvent event) {
            }
        });

        return panel;
    }

    private void styleCountLabel(JLabel label) {
        label.setFont(new Font("SansSerif", Font.BOLD, 48));
        label.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void refreshDashboardData() {

        int myClaimCount = Database.getUserClaimedItems(currentUserEmail).size();
        int allClaimedCount = Database.getClaimedItemsCount();

        ArrayList<String[]> activeClaims = Database.getPendingItemsWithClaimants();
        HashSet<String> uniqueItems = new HashSet<>();
        if (activeClaims != null) {
            for (String[] row : activeClaims) {

                String itemSignature = row[0] + "|" + row[2] + "|" + row[3];
                uniqueItems.add(itemSignature);
            }
        }
        int pendingCount = uniqueItems.size();

        myClaimCountLabel.setText(String.valueOf(myClaimCount));
        allClaimedCountLabel.setText(String.valueOf(allClaimedCount));
        pendingCountLabel.setText(String.valueOf(pendingCount));

        recentItemsModel.setRowCount(0);
        ArrayList<String[]> items = Database.getUnclaimedItems();
        int count = 0;
        for (String[] item : items) {
            if (count >= 10)
                break;

            recentItemsModel.addRow(new Object[] { item[1], item[2], item[3], item[4] });
            count++;
        }
    }

    public void showMyClaimedItems() {
        mainContentPanel.removeAll();
        mainContentPanel.add(new ViewClaimedItems(currentUserEmail));
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    public void showAllClaimedItems() {
        mainContentPanel.removeAll();
        mainContentPanel.add(new ViewClaimedItems(null));
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    public void showViewLostItems() {
        mainContentPanel.removeAll();
        mainContentPanel.add(new ViewLostItemsPanel(currentUserEmail));
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    public void showClaimConfirmation() {
        mainContentPanel.removeAll();
        mainContentPanel.add(new ClaimConfirmationPanel(currentUserEmail));
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    public void showViewPendingItems() {
        mainContentPanel.removeAll();
        mainContentPanel.add(new ViewPendingItemsPanel());
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    public void showMessaging() {
        mainContentPanel.removeAll();
        mainContentPanel.add(messagingPanel);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    public void openMessaging(String recipientName) {
        showMessaging();
        messagingPanel.openChat(recipientName);
    }

    private JPanel createUserInfoBox(String emoji, String name, String role) {
        JPanel box = new JPanel(new FlowLayout(FlowLayout.LEFT));
        box.setMaximumSize(new Dimension(260, 48));
        box.setBackground(new Color(25, 42, 86));
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 15, 0, 15),
                BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(60, 90, 140))));

        JLabel iconLabel = new JLabel(emoji);
        iconLabel.setForeground(Color.WHITE);
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        box.add(iconLabel);

        box.add(Box.createHorizontalStrut(10));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(name);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 12));

        JLabel roleLabel = new JLabel(role);
        roleLabel.setForeground(Color.LIGHT_GRAY);
        roleLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));

        textPanel.add(nameLabel);
        textPanel.add(roleLabel);

        box.add(textPanel);

        box.setCursor(new Cursor(Cursor.HAND_CURSOR));
        box.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showProfile();
            }
        });

        return box;
    }

    private JPanel createNavBox(String emoji, String labelText, String actionCommand) {
        JPanel box = new JPanel(new FlowLayout(FlowLayout.LEFT));
        box.setMaximumSize(new Dimension(260, 48));
        box.setBackground(new Color(25, 42, 86));
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 15, 0, 15),
                BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(60, 90, 140))));

        JLabel iconLabel = new JLabel(emoji);
        iconLabel.setForeground(Color.WHITE);
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));

        JLabel textLabel = new JLabel(labelText);
        textLabel.setForeground(Color.WHITE);
        textLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

        box.add(iconLabel);
        box.add(Box.createHorizontalStrut(10));
        box.add(textLabel);

        box.setCursor(new Cursor(Cursor.HAND_CURSOR));
        box.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                navigate(actionCommand);
            }
        });

        return box;
    }

    private void navigate(String actionCommand) {
        if (actionCommand.equals("LOGOUT")) {
            handleLogout();
        } else if (actionCommand.equals("DASHBOARD")) {
            showDashboard();
        } else if (actionCommand.equals("FAQS")) {
            showFAQ();
        } else if (actionCommand.equals("REPORT_ITEM")) {
            showReportItem();
        } else if (actionCommand.equals("CLAIM_CONFIRM")) {
            showClaimConfirmation();
        } else if (actionCommand.equals("PROFILE")) {
            showProfile();
        } else if (actionCommand.equals("VIEW_ITEM")) {
            showViewLostItems();
        } else if (actionCommand.equals("MY_CLAIMED")) {
            showMyClaimedItems();
        } else if (actionCommand.equals("ALL_CLAIMED")) {
            showAllClaimedItems();
        } else if (actionCommand.equals("VIEW_PENDING")) {
            showViewPendingItems();
        } else if (actionCommand.equals("MESSAGING")) {
            showMessaging();
        }
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to log out?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            new AppFrame().setVisible(true);
        }
    }

    public void showProfile() {
        mainContentPanel.removeAll();
        mainContentPanel.add(new ProfilePanel(currentUserEmail));
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    public void showReportItem() {
        mainContentPanel.removeAll();
        mainContentPanel.add(new ReportMissingItemPanel(currentUserEmail));
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    public void showDashboard() {
        mainContentPanel.removeAll();
        mainContentPanel.add(dashboardPanel);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    public void showFAQ() {
        mainContentPanel.removeAll();
        mainContentPanel.add(new FAQPanel());
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    private JPanel createSummaryBox(String title, JLabel countLabel, String actionCmd) {
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(new Color(230, 240, 255));
        box.setBorder(BorderFactory.createLineBorder(new Color(180, 200, 230), 2));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        titleLabel.setForeground(new Color(25, 42, 86));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));

        JButton viewButton = new JButton("View");
        viewButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        viewButton.setMaximumSize(new Dimension(150, 35));
        viewButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        viewButton.setBackground(new Color(218,164,37));
        viewButton.setForeground(Color.WHITE);
        viewButton.setFocusPainted(false);

        viewButton.addActionListener(e -> navigate(actionCmd));

        box.add(Box.createVerticalStrut(15));
        box.add(countLabel);
        box.add(Box.createVerticalStrut(5));
        box.add(titleLabel);
        box.add(Box.createVerticalStrut(15));

        JPanel btnContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        btnContainer.setOpaque(false);
        btnContainer.add(viewButton);
        btnContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        box.add(btnContainer);
        box.add(Box.createVerticalStrut(15));

        return box;
    }

    private void toggleSidebar(ActionEvent e) {
        sidebarVisible = !sidebarVisible;
        sidebar.setPreferredSize(new Dimension(sidebarVisible ? 260 : 60, getHeight()));

        for (Component c : sidebar.getComponents()) {
            if (c instanceof JPanel) {
                for (Component inner : ((JPanel) c).getComponents()) {

                    if (inner instanceof JLabel) {
                        JLabel label = (JLabel) inner;
                        if (!label.getText().matches("[\\p{So}\\p{Cn}]")) {
                            label.setVisible(sidebarVisible);
                        }
                    }

                    else if (!(inner instanceof JButton)) {
                        inner.setVisible(sidebarVisible);
                    }
                }
            }
        }
        sidebar.revalidate();
        sidebar.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HomePage("test@student.national-u.edu.ph").setVisible(true));
    }
}