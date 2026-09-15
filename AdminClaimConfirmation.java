import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

public class AdminClaimConfirmation extends JPanel {

    private JPanel listPanel;

    public AdminClaimConfirmation() {

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(240, 240, 240));

        JLabel header = new JLabel("PENDING CLAIMS VERIFICATION");
        header.setFont(new Font("SansSerif", Font.BOLD, 26));
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 0));
        headerPanel.add(header);
        add(headerPanel, BorderLayout.NORTH);

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);
        listPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        loadData();

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

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
        listPanel.removeAll();
        try {

            ArrayList<String[]> claims = Database.getPendingItemsWithClaimants();

            if (claims == null || claims.isEmpty()) {
                JLabel empty = new JLabel("No pending claims at the moment.");
                empty.setFont(new Font("SansSerif", Font.PLAIN, 16));
                empty.setForeground(Color.GRAY);
                empty.setAlignmentX(Component.CENTER_ALIGNMENT);
                listPanel.add(empty);
            } else {
                for (String[] claim : claims) {

                    String itemName = claim[0];
                    String claimerName = claim[4];
                    String claimerEmail = (claim.length > 7) ? claim[7] : claimerName;
                    String imgPath = claim[6];

                    String claimId = (claim.length > 8) ? claim[8] : null;

                    if (claimId != null) {
                        listPanel.add(createClaimCard(itemName, claimerName, claimerEmail, imgPath, claim, claimId));
                        listPanel.add(Box.createVerticalStrut(15));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            listPanel.add(new JLabel("Error loading data: " + e.getMessage()));
        }
        listPanel.revalidate();
        listPanel.repaint();
    }

    private JPanel createClaimCard(String item, String claimer, String email, String imgPath, String[] fullData,
            String claimId) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        card.setMaximumSize(new Dimension(800, 140));
        card.setPreferredSize(new Dimension(800, 140));

        JLabel imgLabel = new JLabel();
        imgLabel.setPreferredSize(new Dimension(100, 100));
        imgLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        imgLabel.setHorizontalAlignment(JLabel.CENTER);

        if (imgPath != null && !imgPath.isEmpty()) {
            ImageIcon icon = new ImageIcon(imgPath);
            Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            imgLabel.setIcon(new ImageIcon(img));
        } else {
            imgLabel.setText("No Image");
        }
        card.add(imgLabel, BorderLayout.WEST);

        JPanel details = new JPanel();
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
        details.setBackground(Color.WHITE);
        details.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));

        JLabel nameLbl = new JLabel(item);
        nameLbl.setFont(new Font("SansSerif", Font.BOLD, 18));
        nameLbl.setForeground(new Color(25, 42, 86));

        JLabel claimerLbl = new JLabel("Claimant: " + claimer);
        claimerLbl.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JLabel dateLbl = new JLabel("Found: " + fullData[2]);
        dateLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        dateLbl.setForeground(Color.GRAY);

        details.add(nameLbl);
        details.add(Box.createVerticalStrut(5));
        details.add(claimerLbl);
        details.add(Box.createVerticalStrut(5));
        details.add(dateLbl);

        card.add(details, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setBackground(Color.WHITE);

        JButton approveBtn = new JButton("Approve");
        approveBtn.setBackground(new Color(46, 204, 113));
        approveBtn.setForeground(Color.WHITE);
        approveBtn.setFocusPainted(false);

        JButton rejectBtn = new JButton("Reject");
        rejectBtn.setBackground(new Color(231, 76, 60));
        rejectBtn.setForeground(Color.WHITE);
        rejectBtn.setFocusPainted(false);

        JButton msgBtn = new JButton("Message");
        msgBtn.setBackground(new Color(52, 152, 219));
        msgBtn.setForeground(Color.WHITE);
        msgBtn.setFocusPainted(false);

        JButton detailBtn = new JButton("More Detail");
        detailBtn.setBackground(new Color(241, 196, 15));
        detailBtn.setForeground(Color.BLACK);
        detailBtn.setFocusPainted(false);

        approveBtn.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to APPROVE this claim for " + claimer + "?",
                    "Confirm Approval", JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                if (Database.approveClaim(Integer.parseInt(claimId))) {
                    JOptionPane.showMessageDialog(this, "Claim Approved Successfully!");
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this, "Error approving claim.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        rejectBtn.addActionListener(e -> {
            String reason = JOptionPane.showInputDialog(this,
                    "Enter reason for rejection:",
                    "Reject Claim", JOptionPane.WARNING_MESSAGE);

            if (reason != null && !reason.trim().isEmpty()) {
                if (Database.rejectClaim(Integer.parseInt(claimId), reason.trim())) {
                    JOptionPane.showMessageDialog(this, "Claim Rejected.");
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this, "Error rejecting claim.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else if (reason != null) {

                JOptionPane.showMessageDialog(this, "Rejection reason is required.", "Warning",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        msgBtn.addActionListener(e -> {
            Container parent = getTopLevelAncestor();
            if (parent instanceof AdminDashboard) {
                ((AdminDashboard) parent).openMessaging(email);
            }
        });

        detailBtn.addActionListener(e -> showItemDetails(fullData));

        actions.add(approveBtn);
        actions.add(rejectBtn);
        actions.add(msgBtn);
        actions.add(detailBtn);

        card.add(actions, BorderLayout.EAST);

        return card;
    }

    private void showItemDetails(String[] data) {

        String itemName = data[0];
        String landmark = data[1];
        String dateTime = data[2] + " " + data[3];
        String claimerName = data[4];
        String description = data[5];
        String imgPath = data[6];
        String proof = (data.length > 9) ? data[9] : "No proof provided";
        String reporterName = (data.length > 10) ? data[10] : "Unknown";

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Full Claim Details", true);
        dialog.setSize(500, 650);
        dialog.setLocationRelativeTo(null);
        dialog.setLayout(null);
        dialog.getContentPane().setBackground(Color.WHITE);

        JLabel title = new JLabel("ITEM & CLAIM DETAILS");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(new Color(25, 42, 86));
        title.setBounds(120, 20, 300, 30);
        dialog.add(title);

        JLabel imgLabel = new JLabel();
        imgLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        imgLabel.setBounds(100, 60, 300, 200);
        imgLabel.setHorizontalAlignment(JLabel.CENTER);
        if (imgPath != null && !imgPath.isEmpty()) {
            ImageIcon icon = new ImageIcon(imgPath);
            Image img = icon.getImage().getScaledInstance(300, 200, Image.SCALE_SMOOTH);
            imgLabel.setIcon(new ImageIcon(img));
        } else {
            imgLabel.setText("No Image Available");
        }
        dialog.add(imgLabel);

        int y = 280;
        addDetail(dialog, "Item Name:", itemName, y);
        addDetail(dialog, "Reported By:", reporterName, y + 30);
        addDetail(dialog, "Found At:", landmark, y + 60);
        addDetail(dialog, "Date/Time:", dateTime, y + 90);
        addDetail(dialog, "Claimant:", claimerName, y + 120);

        JLabel proofLbl = new JLabel("Proof:");
        proofLbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        proofLbl.setBounds(50, y + 150, 100, 20);
        dialog.add(proofLbl);

        JTextArea proofArea = new JTextArea(proof);
        proofArea.setWrapStyleWord(true);
        proofArea.setLineWrap(true);
        proofArea.setEditable(false);
        proofArea.setBackground(new Color(245, 248, 250));
        JScrollPane proofScroll = new JScrollPane(proofArea);
        proofScroll.setBounds(150, y + 150, 300, 50);
        dialog.add(proofScroll);

        JLabel descLbl = new JLabel("Description:");
        descLbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        descLbl.setBounds(50, y + 210, 100, 20);
        dialog.add(descLbl);

        JTextArea descArea = new JTextArea(description);
        descArea.setWrapStyleWord(true);
        descArea.setLineWrap(true);
        descArea.setEditable(false);
        descArea.setBackground(new Color(245, 248, 250));
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setBounds(150, y + 210, 300, 50);
        dialog.add(descScroll);

        JButton closeBtn = new JButton("Close");
        closeBtn.setBounds(200, 550, 100, 40);
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