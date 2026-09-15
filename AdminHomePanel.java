import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.table.DefaultTableModel;

public class AdminHomePanel extends JPanel {

    private JPanel contentArea;
    private CardLayout cardLayout;

    private JLabel unclaimedCountLabel;
    private JLabel pendingAccountsCountLabel;
    private JLabel pendingClaimsCountLabel;

    public AdminHomePanel(JPanel contentArea, CardLayout cardLayout) {
        this.contentArea = contentArea;
        this.cardLayout = cardLayout;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(240, 240, 240));
        JLabel header = new JLabel("ADMIN DASHBOARD");
        header.setFont(new Font("SansSerif", Font.BOLD, 26));
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 0));
        headerPanel.add(header);
        add(headerPanel, BorderLayout.NORTH);

        JPanel mainContent = new JPanel(new BorderLayout(0, 30));
        mainContent.setBackground(Color.WHITE);
        mainContent.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel summaryGrid = new JPanel(new GridLayout(1, 3, 20, 0));
        summaryGrid.setBackground(Color.WHITE);

        unclaimedCountLabel = new JLabel("...", SwingConstants.LEFT);
        pendingAccountsCountLabel = new JLabel("...", SwingConstants.LEFT);
        pendingClaimsCountLabel = new JLabel("...", SwingConstants.LEFT);

        styleCountLabel(unclaimedCountLabel);
        styleCountLabel(pendingAccountsCountLabel);
        styleCountLabel(pendingClaimsCountLabel);

        summaryGrid.add(createSummaryBox("Unclaimed Items", unclaimedCountLabel, "UNCLAIMED_ITEMS"));
        summaryGrid.add(createSummaryBox("Pending Accounts", pendingAccountsCountLabel, "PENDING_ACCOUNTS"));
        summaryGrid.add(createSummaryBox("View Pending Claims", pendingClaimsCountLabel, "PENDING_CLAIMS"));

        JPanel summaryWrapper = new JPanel(new BorderLayout());
        summaryWrapper.setBackground(Color.WHITE);
        summaryWrapper.add(summaryGrid, BorderLayout.CENTER);
        summaryWrapper.setPreferredSize(new Dimension(0, 180));

        mainContent.add(summaryWrapper, BorderLayout.NORTH);

        JPanel tablePanel = new JPanel(new BorderLayout(0, 10));
        tablePanel.setBackground(Color.WHITE);

        JLabel tableLabel = new JLabel("Displaying most recent registered accounts");
        tableLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        tableLabel.setForeground(new Color(25, 42, 86));
        tablePanel.add(tableLabel, BorderLayout.NORTH);

        String[] columns = { "Student No.", "Full Name", "Email", "Department", "User Level" };
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(25, 42, 86));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(230, 240, 255));
        table.setSelectionForeground(Color.BLACK);

        refreshTableData(model);

        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        mainContent.add(tablePanel, BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);

        refreshCounts();

        addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                refreshCounts();
                refreshTableData(model);
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {
            }

            @Override
            public void ancestorMoved(AncestorEvent event) {
            }
        });
    }

    private void styleCountLabel(JLabel label) {
        label.setFont(new Font("SansSerif", Font.BOLD, 48));
        label.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void refreshCounts() {

        int pendingAccountsCount = Database.getPendingUserCount();

        ArrayList<String[]> activeClaims = Database.getPendingItemsWithClaimants();
        HashSet<String> uniquePendingItems = new HashSet<>();
        if (activeClaims != null) {
            for (String[] row : activeClaims) {
                uniquePendingItems.add(row[0] + "|" + row[2] + "|" + row[3]);
            }
        }
        int uniquePendingClaimsCount = uniquePendingItems.size();

        ArrayList<String[]> allItems = Database.getUnclaimedItems();

        ArrayList<String[]> pendingClaims = Database.getPendingClaims();
        ArrayList<String[]> approvedClaims = Database.getAllClaimedItems();
        ArrayList<String> restrictedNames = new ArrayList<>();

        if (pendingClaims != null) {
            for (String[] c : pendingClaims)
                restrictedNames.add(c[1]);
        }
        if (approvedClaims != null) {
            for (String[] c : approvedClaims)
                restrictedNames.add(c[1]);
        }

        int trueUnclaimedCount = 0;
        for (String[] item : allItems) {
            if (!restrictedNames.contains(item[1])) {
                trueUnclaimedCount++;
            }
        }

        unclaimedCountLabel.setText(String.valueOf(trueUnclaimedCount));
        pendingAccountsCountLabel.setText(String.valueOf(pendingAccountsCount));
        pendingClaimsCountLabel.setText(String.valueOf(uniquePendingClaimsCount));
    }

    private void refreshTableData(DefaultTableModel model) {
        model.setRowCount(0);
        ArrayList<String[]> users = Database.getRecentUsers();
        for (String[] user : users) {
            model.addRow(user);
        }
    }

    private JPanel createSummaryBox(String title, JLabel countLabel, String targetCard) {
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

        viewButton.addActionListener(e -> {
            if (cardLayout != null && contentArea != null) {
                cardLayout.show(contentArea, targetCard);
            }
        });

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
}