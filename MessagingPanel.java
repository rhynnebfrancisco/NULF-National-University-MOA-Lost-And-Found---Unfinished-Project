import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

public class MessagingPanel extends JPanel {

    private String currentUserEmail;
    private JPanel sidebarList;
    private JPanel chatArea;
    private JLabel chatHeaderLabel;
    private JTextArea chatHistory;
    private JTextField inputField;
    private JButton sendButton;
    private JPanel activeChatPanel;
    private JLabel emptyStateLabel;
    private String currentRecipient = null;
    private Timer refreshTimer;

    public MessagingPanel(String userEmail) {
        this.currentUserEmail = userEmail;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(240, 240, 240));
        JLabel header = new JLabel("MESSAGING");
        header.setFont(new Font("SansSerif", Font.BOLD, 26));
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 0));
        headerPanel.add(header);
        add(headerPanel, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(280);
        splitPane.setDividerSize(1);
        splitPane.setBorder(null);

        JPanel sidebarPanel = new JPanel(new BorderLayout());
        sidebarPanel.setBackground(new Color(245, 248, 250));

        JLabel listHeader = new JLabel("  Conversations");
        listHeader.setFont(new Font("SansSerif", Font.BOLD, 16));
        listHeader.setForeground(new Color(0x3B468F));
        listHeader.setPreferredSize(new Dimension(0, 40));
        sidebarPanel.add(listHeader, BorderLayout.NORTH);

        sidebarList = new JPanel();
        sidebarList.setLayout(new BoxLayout(sidebarList, BoxLayout.Y_AXIS));
        sidebarList.setBackground(new Color(245, 248, 250));
        JScrollPane sidebarScroll = new JScrollPane(sidebarList);
        sidebarScroll.setBorder(null);
        sidebarPanel.add(sidebarScroll, BorderLayout.CENTER);

        splitPane.setLeftComponent(sidebarPanel);

        chatArea = new JPanel(new CardLayout());

        emptyStateLabel = new JLabel("Select a conversation to start chatting", SwingConstants.CENTER);
        emptyStateLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        emptyStateLabel.setForeground(Color.GRAY);
        chatArea.add(emptyStateLabel, "EMPTY");

        activeChatPanel = createChatUI();
        chatArea.add(activeChatPanel, "CHAT");

        splitPane.setRightComponent(chatArea);
        add(splitPane, BorderLayout.CENTER);

        refreshConversations();

        refreshTimer = new Timer(1000, e -> {
            refreshConversations();
            if (currentRecipient != null) {
                loadMessages();
            }
        });
        refreshTimer.start();
    }

    private JPanel createChatUI() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        chatHeaderLabel = new JLabel(" ");
        chatHeaderLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        chatHeaderLabel.setForeground(new Color(0x3B468F));
        chatHeaderLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        panel.add(chatHeaderLabel, BorderLayout.NORTH);

        chatHistory = new JTextArea();
        chatHistory.setEditable(false);
        chatHistory.setFont(new Font("SansSerif", Font.PLAIN, 14));
        chatHistory.setLineWrap(true);
        chatHistory.setWrapStyleWord(true);
        chatHistory.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(chatHistory);
        scrollPane.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.LIGHT_GRAY));
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        inputPanel.setBackground(Color.WHITE);

        inputField = new JTextField();
        inputField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        inputField.setPreferredSize(new Dimension(0, 40));

        sendButton = new JButton("Send");
        sendButton.setBackground(new Color(46, 204, 113));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        sendButton.setFocusPainted(false);

        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        panel.add(inputPanel, BorderLayout.SOUTH);

        return panel;
    }

    public void openChat(String recipientEmail) {
        if (recipientEmail == null || recipientEmail.equals(currentUserEmail))
            return;

        String displayName = Database.getUserName(recipientEmail);

        openChat(recipientEmail, displayName);
    }

    private void openChat(String recipientEmail, String recipientName) {
        if (recipientEmail == null)
            return;

        this.currentRecipient = recipientEmail;
        chatHeaderLabel.setText("Chat with: " + recipientName);

        CardLayout cl = (CardLayout) chatArea.getLayout();
        cl.show(chatArea, "CHAT");

        loadMessages();
        inputField.requestFocus();

        refreshConversations();
    }

    private void loadMessages() {
        if (currentRecipient == null)
            return;

        JScrollPane scroll = (JScrollPane) activeChatPanel.getComponent(1);
        JScrollBar vertical = scroll.getVerticalScrollBar();
        boolean atBottom = (vertical.getValue() + vertical.getVisibleAmount() >= vertical.getMaximum() - 20);

        ArrayList<String[]> msgs = Database.getMessages(currentUserEmail, currentRecipient);
        StringBuilder sb = new StringBuilder();

        for (String[] m : msgs) {
            String sender = m[0];
            String text = m[1];

            if (sender.equals(currentUserEmail)) {
                sb.append("You: ").append(text).append("\n\n");
            } else {

                String senderName = (sender.equals(currentUserEmail)) ? "You" : Database.getUserName(sender);

                if (Database.isAdmin(sender)) {
                    senderName += " [ADMIN]";
                }

                sb.append(senderName).append(": ").append(text).append("\n\n");
            }
        }

        if (!chatHistory.getText().equals(sb.toString())) {
            chatHistory.setText(sb.toString());

            if (atBottom) {
                SwingUtilities.invokeLater(() -> vertical.setValue(vertical.getMaximum()));
            }
        }
    }

    private void refreshConversations() {

        ArrayList<String[]> partners = Database.getConversationPartnersInfo(currentUserEmail);

        boolean currentExists = false;
        for (String[] p : partners) {
            if (p[0].equals(currentRecipient)) {
                currentExists = true;
                break;
            }
        }

        if (currentRecipient != null && !currentExists) {

            String name = Database.getUserName(currentRecipient);
            partners.add(0, new String[] { currentRecipient, name });
        }

        boolean needsUpdate = false;

        if (sidebarList.getComponentCount() != partners.size()) {
            needsUpdate = true;
        } else {

            for (int i = 0; i < partners.size(); i++) {
                JButton btn = (JButton) sidebarList.getComponent(i);

                if (!btn.getText().equals(partners.get(i)[1])) {
                    needsUpdate = true;
                    break;
                }
            }
        }

        if (needsUpdate) {
            sidebarList.removeAll();

            for (String[] partner : partners) {
                String email = partner[0];
                String name = partner[1];

                JButton btn = new JButton(name);
                btn.setAlignmentX(Component.LEFT_ALIGNMENT);
                btn.setMaximumSize(new Dimension(280, 50));
                btn.setPreferredSize(new Dimension(280, 50));
                btn.setBackground(new Color(255, 255, 255));
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                        BorderFactory.createEmptyBorder(10, 15, 10, 15)));
                btn.setFocusPainted(false);
                btn.setHorizontalAlignment(SwingConstants.LEFT);
                btn.setFont(new Font("SansSerif", Font.PLAIN, 14));

                btn.putClientProperty("email", email);

                if (email.equals(currentRecipient)) {
                    btn.setBackground(new Color(230, 240, 255));
                }

                btn.addActionListener(e -> openChat(email, name));
                sidebarList.add(btn);
            }

            sidebarList.revalidate();
            sidebarList.repaint();
        } else {

            for (Component c : sidebarList.getComponents()) {
                JButton btn = (JButton) c;
                String btnEmail = (String) btn.getClientProperty("email");

                if (btnEmail != null && btnEmail.equals(currentRecipient)) {
                    btn.setBackground(new Color(230, 240, 255));
                } else {
                    btn.setBackground(Color.WHITE);
                }
            }
            sidebarList.repaint();
        }
    }

    private void sendMessage() {
        String msg = inputField.getText().trim();
        if (!msg.isEmpty() && currentRecipient != null) {

            boolean sent = Database.sendMessage(currentUserEmail, currentRecipient, msg);
            if (sent) {
                inputField.setText("");
                loadMessages();
            }
        }
    }
}