import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class AdminDashboard extends JFrame {

    private AppFrame loginFrame;
    private JPanel sidebar;
    private boolean sidebarVisible = true;
    private JPanel contentArea;
    private CardLayout cardLayout;
    private String currentAdminEmail;

    private MessagingPanel messagingPanel;
    private final Color SIDEBAR_COLOR = new Color(25, 42, 86);
    private JPanel userInfoBox;
    private AdminGraphAnalytics graphAnalytics;

    public AdminDashboard(AppFrame loginFrame) {
        this(loginFrame, "admin@nu-moa.edu.ph");
    }

    public AdminDashboard(AppFrame loginFrame, String adminEmail) {
        this.loginFrame = loginFrame;
        this.currentAdminEmail = adminEmail;

        setTitle("NULF - Admin Dashboard");
        setSize(1150, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        ImageIcon logoIcon = new ImageIcon(getClass().getResource("Logo-NULF.png"));
        setIconImage(logoIcon.getImage());

        sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR_COLOR);
        sidebar.setPreferredSize(new Dimension(260, getHeight()));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JPanel menuHeader = new JPanel(new FlowLayout(FlowLayout.LEFT));
        menuHeader.setBackground(SIDEBAR_COLOR);
        menuHeader.setMaximumSize(new Dimension(260, 55));

        JButton hamburgerBtn = new JButton("☰");
        hamburgerBtn.setFont(new Font("SansSerif", Font.BOLD, 20));
        hamburgerBtn.setForeground(Color.WHITE);
        hamburgerBtn.setBackground(SIDEBAR_COLOR);
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

        refreshUserInfo();

        sidebar.add(createNavBox("📊", "DASHBOARD", "DASHBOARD"));
        sidebar.add(createNavBox("👥", "MANAGE USERS", "VIEW_USERS"));
        sidebar.add(createNavBox("📜", "VIEW CLAIMED ITEMS", "CLAIMED_HISTORY"));
        sidebar.add(createNavBox("📝", "VIEW PENDING CLAIMS", "PENDING_CLAIMS"));
        sidebar.add(createNavBox("📊", "GRAPH ANALYTICS", "GRAPH_ANALYTICS"));
        sidebar.add(createNavBox("💬", "MESSAGING", "MESSAGING"));

        sidebar.add(Box.createVerticalGlue());
        sidebar.add(createNavBox("🚪", "LOGOUT", "LOGOUT"));
        sidebar.add(Box.createVerticalStrut(20));

        cardLayout = new CardLayout();
        contentArea = new JPanel(cardLayout);
        contentArea.setBackground(Color.WHITE);

        contentArea.add(new AdminHomePanel(contentArea, cardLayout), "DASHBOARD");
        contentArea.add(new AdminUserManagement(contentArea, cardLayout), "VIEW_USERS");
        contentArea.add(new AdminPendingAccounts(), "PENDING_ACCOUNTS");
        contentArea.add(new AdminUnclaimedItems(), "UNCLAIMED_ITEMS");
        contentArea.add(new AdminClaimedHistory(), "CLAIMED_HISTORY");
        
        graphAnalytics = new AdminGraphAnalytics();
        contentArea.add(new AdminGraphAnalytics(), "GRAPH_ANALYTICS");

        try {
            contentArea.add(new AdminClaimConfirmation(), "PENDING_CLAIMS");
        } catch (Exception e) {
            System.err.println("Error initializing AdminClaimConfirmation: " + e.getMessage());
            e.printStackTrace();
            contentArea.add(createPlaceholderPanel("Error loading Pending Claims"), "PENDING_CLAIMS");
        }

        messagingPanel = new MessagingPanel(adminEmail);
        contentArea.add(messagingPanel, "MESSAGING");

        AdminProfilePanel profilePanel = new AdminProfilePanel(adminEmail, this::refreshUserInfo);
        contentArea.add(profilePanel, "PROFILE");

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(sidebar, BorderLayout.WEST);
        getContentPane().add(contentArea, BorderLayout.CENTER);

        setVisible(true);
    }

    public void refreshUserInfo() {

        String[] details = Database.getUserDetails(currentAdminEmail);
        String adminName = (details != null) ? details[0] + " " + details[1] : "SYSTEM ADMIN";

        if (userInfoBox != null) {
            sidebar.remove(userInfoBox);
        }

        JPanel newBox = createUserInfoBox("👤", adminName.toUpperCase(), "ADMINISTRATOR");

        if (sidebar.getComponentCount() >= 2) {
            sidebar.add(newBox, 2);
        } else {
            sidebar.add(newBox);
        }

        this.userInfoBox = newBox;
        sidebar.revalidate();
        sidebar.repaint();
    }

    public void openMessaging(String recipientEmail) {
        cardLayout.show(contentArea, "MESSAGING");
        if (messagingPanel != null) {
            messagingPanel.openChat(recipientEmail);
        }
    }

    private JPanel createUserInfoBox(String emoji, String name, String role) {
        JPanel box = new JPanel(new FlowLayout(FlowLayout.LEFT));
        box.setMaximumSize(new Dimension(260, 48));
        box.setBackground(SIDEBAR_COLOR);
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
                cardLayout.show(contentArea, "PROFILE");
            }
        });
        return box;
    }

    private JPanel createNavBox(String emoji, String labelText, String cardName) {
        JPanel box = new JPanel(new FlowLayout(FlowLayout.LEFT));
        box.setMaximumSize(new Dimension(260, 48));
        box.setBackground(SIDEBAR_COLOR);
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
                if (cardName.equals("LOGOUT")) {
                    handleLogout();
                } else {
                    cardLayout.show(contentArea, cardName);
                }
            }
        });
        return box;
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
                    } else if (!(inner instanceof JButton)) {
                        inner.setVisible(sidebarVisible);
                    }
                }
            }
        }
        sidebar.revalidate();
        sidebar.repaint();
    }

    private JPanel createPlaceholderPanel(String title) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        JLabel l = new JLabel(title + " Panel Coming Soon", SwingConstants.CENTER);
        l.setFont(new Font("SansSerif", Font.BOLD, 24));
        p.add(l, BorderLayout.CENTER);
        return p;
    }
}