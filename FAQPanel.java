import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class FAQPanel extends JPanel {

    public FAQPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Frequently Asked Questions");
        title.setFont(new Font("SansSerif", Font.BOLD, 30));
        title.setForeground(new Color(25, 42, 86));
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 30, 0));
        add(title, BorderLayout.NORTH);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);

        listPanel.add(new FAQItem("How do I report a missing item?",
                "Go to the 'Report Missing Items' tab in the sidebar. Fill out the form with the item name, category, date lost, and location. You can also upload a photo if you have one."));
        listPanel.add(Box.createVerticalStrut(10));

        listPanel.add(new FAQItem("How do I claim an item I found in the list?",
                "Navigate to 'View Lost Item'. If you see your item, click the 'View' button and then select 'Claim'. You will be asked to provide specific details (like a description or wallpaper image) to prove ownership."));
        listPanel.add(Box.createVerticalStrut(10));

        listPanel.add(new FAQItem("Where is the Lost & Found office located?",
                "We are located at the NU MOA Administration Office on the Ground Floor, near the elevators."));
        listPanel.add(Box.createVerticalStrut(10));

        listPanel.add(new FAQItem("How long does the school keep lost items?",
                "Standard items are kept for 60 days. Valuables (electronics, wallets) are kept for 90 days. Unclaimed items after this period are donated to partner charities."));
        listPanel.add(Box.createVerticalStrut(10));

        listPanel.add(new FAQItem("I found an item, what should I do?",
                "Please surrender the item immediately to the nearest Security Guard or bring it directly to the Lost & Found office so we can log it in the system."));
        listPanel.add(Box.createVerticalStrut(10));

        listPanel.add(new FAQItem("Can I update my profile information?",
                "Currently, some profile details are locked to your student record. If you need to change your section or program, please contact the administrator."));

        listPanel.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane, BorderLayout.CENTER);
    }

    private class FAQItem extends JPanel {
        private boolean isExpanded = false;
        private JPanel answerPanel;
        private JLabel arrowLabel;

        public FAQItem(String question, String answer) {
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);

            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(new Color(245, 248, 250));
            headerPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(220, 220, 220)),
                    BorderFactory.createEmptyBorder(15, 20, 15, 20)));
            headerPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

            JLabel questionLabel = new JLabel(question);
            questionLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
            questionLabel.setForeground(new Color(25, 42, 86));

            arrowLabel = new JLabel("▶");
            arrowLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            arrowLabel.setForeground(Color.GRAY);

            headerPanel.add(questionLabel, BorderLayout.CENTER);
            headerPanel.add(arrowLabel, BorderLayout.EAST);

            answerPanel = new JPanel(new BorderLayout());
            answerPanel.setBackground(Color.WHITE);
            answerPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 1, 1, 1, new Color(220, 220, 220)),
                    BorderFactory.createEmptyBorder(15, 20, 15, 20)));

            JTextArea answerText = new JTextArea(answer);
            answerText.setFont(new Font("SansSerif", Font.PLAIN, 15));
            answerText.setForeground(Color.DARK_GRAY);
            answerText.setLineWrap(true);
            answerText.setWrapStyleWord(true);
            answerText.setEditable(false);
            answerText.setOpaque(false);

            answerPanel.add(answerText, BorderLayout.CENTER);
            answerPanel.setVisible(false);

            add(headerPanel, BorderLayout.NORTH);
            add(answerPanel, BorderLayout.CENTER);

            headerPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    toggle();
                }
            });
        }

        private void toggle() {
            isExpanded = !isExpanded;
            answerPanel.setVisible(isExpanded);
            arrowLabel.setText(isExpanded ? "▼" : "▶");

            revalidate();
            repaint();

            Container parent = getParent();
            if (parent != null) {
                parent.revalidate();
                parent.repaint();
            }
        }

        @Override
        public Dimension getMaximumSize() {
            return new Dimension(super.getMaximumSize().width, getPreferredSize().height);
        }
    }
}