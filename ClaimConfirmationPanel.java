import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class ClaimConfirmationPanel extends JPanel {

    private JPanel listPanel;
    private JPanel paginationPanel;
    private JLabel pageIndicator;
    private JButton prevButton;
    private JButton nextButton;

    private ArrayList<String[]> allClaims;
    private int currentPage = 1;
    private final int ITEMS_PER_PAGE = 5;

    public ClaimConfirmationPanel(String userEmail) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(240, 240, 240));
        JLabel header = new JLabel("NOTIFICATIONS");
        header.setFont(new Font("SansSerif", Font.BOLD, 26));
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 0));
        headerPanel.add(header);
        add(headerPanel, BorderLayout.NORTH);

        // List Container
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);
        listPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Fetch Data
        allClaims = Database.getUserClaims(userEmail);

        // Pagination Controls
        setupPaginationPanel();
        add(paginationPanel, BorderLayout.SOUTH);

        // Initial Render
        renderPage();
    }

    private void setupPaginationPanel() {
        paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        paginationPanel.setBackground(new Color(245, 245, 245));
        paginationPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));

        prevButton = new JButton("Previous");
        nextButton = new JButton("Next");
        pageIndicator = new JLabel("Page 1 of 1");
        pageIndicator.setFont(new Font("SansSerif", Font.BOLD, 14));

        stylePaginationButton(prevButton);
        stylePaginationButton(nextButton);

        prevButton.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                renderPage();
            }
        });

        nextButton.addActionListener(e -> {
            int totalPages = (int) Math.ceil((double) allClaims.size() / ITEMS_PER_PAGE);
            if (currentPage < totalPages) {
                currentPage++;
                renderPage();
            }
        });

        paginationPanel.add(prevButton);
        paginationPanel.add(pageIndicator);
        paginationPanel.add(nextButton);

        // Hide if no items or only one page
        updatePaginationVisibility();
    }

    private void stylePaginationButton(JButton btn) {
        btn.setFocusPainted(false);
        btn.setBackground(new Color(0x3B468F));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(100, 30));
    }

    private void renderPage() {
        listPanel.removeAll();

        if (allClaims.isEmpty()) {
            JLabel noData = new JLabel("No claim requests found.");
            noData.setFont(new Font("SansSerif", Font.PLAIN, 16));
            noData.setAlignmentX(Component.CENTER_ALIGNMENT);
            listPanel.add(noData);
        } else {
            int startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
            int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, allClaims.size());

            List<String[]> currentItems = allClaims.subList(startIndex, endIndex);

            for (int i = 0; i < currentItems.size(); i++) {
                String[] c = currentItems.get(i);
                // Expand the first item of every page by default
                boolean isExpanded = (i == 0);

                String status = c[6];
                if ("REJECTED".equalsIgnoreCase(status)) {
                    listPanel.add(new RejectedClaimCard(c, isExpanded));
                } else {
                    listPanel.add(new ClaimItemPanel(c, isExpanded));
                }
                listPanel.add(Box.createVerticalStrut(15));
            }
        }

        listPanel.add(Box.createVerticalGlue());
        
        // Update Indicator and Buttons
        int totalPages = Math.max(1, (int) Math.ceil((double) allClaims.size() / ITEMS_PER_PAGE));
        pageIndicator.setText("Page " + currentPage + " of " + totalPages);
        prevButton.setEnabled(currentPage > 1);
        nextButton.setEnabled(currentPage < totalPages);

        updatePaginationVisibility();

        listPanel.revalidate();
        listPanel.repaint();
        // Scroll back to top
        SwingUtilities.invokeLater(() -> {
            Component c = getComponent(1); // Usually the scrollpane
            if (c instanceof JScrollPane) {
                ((JScrollPane) c).getVerticalScrollBar().setValue(0);
            }
        });
    }

    private void updatePaginationVisibility() {
        if (allClaims.size() <= ITEMS_PER_PAGE) {
            paginationPanel.setVisible(false);
        } else {
            paginationPanel.setVisible(true);
        }
    }

    private class ClaimItemPanel extends JPanel {
        private boolean isExpanded;
        private JPanel detailsPanel;
        private JLabel arrowLabel;

        public ClaimItemPanel(String[] claimData, boolean startExpanded) {
            this.isExpanded = startExpanded;
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));

            String claimId = claimData[0];
            String itemId = claimData[1];
            String itemName = claimData[2];
            String desc = claimData[3];
            String imgPath = claimData[4];
            String date = claimData[5];
            String status = claimData[6];

            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(new Color(245, 248, 250));
            headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            headerPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            headerPanel.setPreferredSize(new Dimension(0, 60));

            JPanel leftInfo = new JPanel(new GridLayout(2, 1));
            leftInfo.setOpaque(false);
            JLabel titleLabel = new JLabel("Transaction Reference No. #" + claimId);
            titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
            titleLabel.setForeground(new Color(0x3B468F));

            JLabel dateLabel = new JLabel("Created: " + date);
            dateLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
            dateLabel.setForeground(Color.GRAY);

            leftInfo.add(titleLabel);
            leftInfo.add(dateLabel);

            JPanel rightInfo = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            rightInfo.setOpaque(false);

            JLabel statusLabel = new JLabel(status);
            statusLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            if ("APPROVED".equalsIgnoreCase(status))
                statusLabel.setForeground(new Color(46, 204, 113));
            else
                statusLabel.setForeground(new Color(243, 156, 18));

            arrowLabel = new JLabel(isExpanded ? "▼" : "▶");
            arrowLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
            arrowLabel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));

            rightInfo.add(statusLabel);
            rightInfo.add(arrowLabel);

            headerPanel.add(leftInfo, BorderLayout.CENTER);
            headerPanel.add(rightInfo, BorderLayout.EAST);

            detailsPanel = new JPanel(null);
            detailsPanel.setBackground(Color.WHITE);
            detailsPanel.setPreferredSize(new Dimension(0, 220));
            detailsPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));

            addDetailLabel(detailsPanel, "Item ID:", itemId, 20, 20);
            addDetailLabel(detailsPanel, "Name of Item:", itemName, 20, 50);

            JLabel descLbl = new JLabel("Description:");
            descLbl.setFont(new Font("SansSerif", Font.BOLD, 14));
            descLbl.setForeground(Color.DARK_GRAY);
            descLbl.setBounds(20, 80, 100, 20);
            detailsPanel.add(descLbl);

            JTextArea descArea = new JTextArea(desc);
            descArea.setWrapStyleWord(true);
            descArea.setLineWrap(true);
            descArea.setEditable(false);
            descArea.setOpaque(false);
            descArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
            JScrollPane descScroll = new JScrollPane(descArea);
            descScroll.setBorder(null);
            descScroll.setBounds(20, 105, 400, 80);
            detailsPanel.add(descScroll);

            String notifMsg = "APPROVED".equalsIgnoreCase(status)
                    ? "Your claim has been approved. Please visit the admin office."
                    : "Claim request filed. Waiting for admin verification.";

            JLabel notifLabel = new JLabel("<html><i>" + notifMsg + "</i></html>");
            notifLabel.setForeground(Color.BLUE);
            notifLabel.setBounds(20, 190, 400, 20);
            detailsPanel.add(notifLabel);

            JLabel imgLabel = new JLabel();
            imgLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            imgLabel.setBounds(500, 10, 250, 190);
            imgLabel.setHorizontalAlignment(JLabel.CENTER);

            if (imgPath != null && !imgPath.isEmpty()) {
                try {
                    ImageIcon icon = new ImageIcon(imgPath);
                    Image img = icon.getImage().getScaledInstance(250, 190, Image.SCALE_SMOOTH);
                    imgLabel.setIcon(new ImageIcon(img));
                } catch (Exception ex) {
                    imgLabel.setText("Image Error");
                }
            } else {
                imgLabel.setText("No Image");
            }
            detailsPanel.add(imgLabel);

            detailsPanel.setVisible(isExpanded);

            add(headerPanel, BorderLayout.NORTH);
            add(detailsPanel, BorderLayout.CENTER);

            headerPanel.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    toggle();
                }
            });
        }

        private void toggle() {
            isExpanded = !isExpanded;
            detailsPanel.setVisible(isExpanded);
            arrowLabel.setText(isExpanded ? "▼" : "▶");
            revalidate();
            repaint();
            Container parent = getParent();
            if (parent != null) {
                parent.revalidate();
                parent.repaint();
            }
        }
    }

    private class RejectedClaimCard extends JPanel {
        private boolean isExpanded;
        private JPanel detailsPanel;
        private JLabel arrowLabel;

        public RejectedClaimCard(String[] claimData, boolean startExpanded) {
            this.isExpanded = startExpanded;
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(new Color(231, 76, 60), 1));

            String claimId = claimData[0];
            String itemId = claimData[1];
            String itemName = claimData[2];
            String desc = claimData[3];
            String imgPath = claimData[4];
            String date = claimData[5];
            String status = claimData[6];

            String rejectionReason = (claimData.length > 7 && claimData[7] != null)
                    ? claimData[7]
                    : "Admin did not provide a specific reason.";

            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(new Color(255, 230, 230));
            headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            headerPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            headerPanel.setPreferredSize(new Dimension(0, 60));

            JPanel leftInfo = new JPanel(new GridLayout(2, 1));
            leftInfo.setOpaque(false);
            JLabel titleLabel = new JLabel("Transaction Reference No. #" + claimId);
            titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
            titleLabel.setForeground(new Color(192, 57, 43));

            JLabel dateLabel = new JLabel("Requested: " + date);
            dateLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
            dateLabel.setForeground(Color.GRAY);

            leftInfo.add(titleLabel);
            leftInfo.add(dateLabel);

            JPanel rightInfo = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            rightInfo.setOpaque(false);

            JLabel statusLabel = new JLabel(status);
            statusLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            statusLabel.setForeground(new Color(231, 76, 60));

            arrowLabel = new JLabel(isExpanded ? "▼" : "▶");
            arrowLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));

            rightInfo.add(statusLabel);
            rightInfo.add(arrowLabel);

            headerPanel.add(leftInfo, BorderLayout.CENTER);
            headerPanel.add(rightInfo, BorderLayout.EAST);

            detailsPanel = new JPanel(null);
            detailsPanel.setBackground(Color.WHITE);
            detailsPanel.setPreferredSize(new Dimension(0, 250));
            detailsPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));

            addDetailLabel(detailsPanel, "Item ID:", itemId, 20, 20);
            addDetailLabel(detailsPanel, "Name of Item:", itemName, 20, 50);

            JLabel descLbl = new JLabel("Description:");
            descLbl.setFont(new Font("SansSerif", Font.BOLD, 14));
            descLbl.setForeground(Color.DARK_GRAY);
            descLbl.setBounds(20, 80, 100, 20);
            detailsPanel.add(descLbl);

            JTextArea descArea = new JTextArea(desc);
            descArea.setWrapStyleWord(true);
            descArea.setLineWrap(true);
            descArea.setEditable(false);
            descArea.setOpaque(false);
            descArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
            JScrollPane descScroll = new JScrollPane(descArea);
            descScroll.setBorder(null);
            descScroll.setBounds(20, 105, 400, 60);
            detailsPanel.add(descScroll);

            JLabel reasonLbl = new JLabel("Admin Response / Rejection Reason:");
            reasonLbl.setFont(new Font("SansSerif", Font.BOLD, 14));
            reasonLbl.setForeground(new Color(192, 57, 43));
            reasonLbl.setBounds(20, 175, 300, 20);
            detailsPanel.add(reasonLbl);

            JTextArea reasonArea = new JTextArea(rejectionReason);
            reasonArea.setWrapStyleWord(true);
            reasonArea.setLineWrap(true);
            reasonArea.setEditable(false);
            reasonArea.setOpaque(false);
            reasonArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
            reasonArea.setForeground(Color.DARK_GRAY);
            reasonArea.setBounds(20, 200, 400, 40);
            detailsPanel.add(reasonArea);

            JLabel imgLabel = new JLabel();
            imgLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            imgLabel.setBounds(500, 10, 250, 220);
            imgLabel.setHorizontalAlignment(JLabel.CENTER);

            if (imgPath != null && !imgPath.isEmpty()) {
                try {
                    ImageIcon icon = new ImageIcon(imgPath);
                    Image img = icon.getImage().getScaledInstance(250, 220, Image.SCALE_SMOOTH);
                    imgLabel.setIcon(new ImageIcon(img));
                } catch (Exception ex) {
                    imgLabel.setText("Image Error");
                }
            } else {
                imgLabel.setText("No Image");
            }
            detailsPanel.add(imgLabel);

            detailsPanel.setVisible(isExpanded);

            add(headerPanel, BorderLayout.NORTH);
            add(detailsPanel, BorderLayout.CENTER);

            headerPanel.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    toggle();
                }
            });
        }

        private void toggle() {
            isExpanded = !isExpanded;
            detailsPanel.setVisible(isExpanded);
            arrowLabel.setText(isExpanded ? "▼" : "▶");
            revalidate();
            repaint();
            Container parent = getParent();
            if (parent != null) {
                parent.revalidate();
                parent.repaint();
            }
        }
    }

    private void addDetailLabel(JPanel p, String title, String val, int x, int y) {
        JLabel t = new JLabel(title);
        t.setFont(new Font("SansSerif", Font.BOLD, 14));
        t.setForeground(Color.DARK_GRAY);
        t.setBounds(x, y, 120, 20);
        p.add(t);

        JLabel v = new JLabel(val);
        v.setFont(new Font("SansSerif", Font.PLAIN, 14));
        v.setBounds(x + 130, y, 300, 20);
        p.add(v);
    }
}