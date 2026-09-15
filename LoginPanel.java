import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class LoginPanel extends JPanel {

    private AppFrame frame;
    private JTextField emailField;
    private JPasswordField passwordField;

    public LoginPanel(AppFrame frame) {
        this.frame = frame;

        Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("Logo-NULF.png"));
        frame.setIconImage(icon);
        
        setLayout(null);
        setBackground(new Color(0x35, 0x40, 0x8F));
        setBounds(0, 0, 1150, 680);

        try {
            ImageIcon logoIcon = new ImageIcon("Logo-NULF.png");
            Image scaledLogo = logoIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            JLabel mainLogo = new JLabel(new ImageIcon(scaledLogo));
            mainLogo.setBounds(70, 150, 200, 200); 
            add(mainLogo);
        } catch (Exception e) {
            System.out.println("Logo file not found.");
        }

        JLabel descriptionLabel = new JLabel("Helping you find lost items and reunite them with their owners.");
        descriptionLabel.setForeground(Color.WHITE);
        descriptionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        descriptionLabel.setBounds(80, 250, 650, 100);

        add(descriptionLabel);

        JPanel cardPanel = new JPanel() {
            private static final int RADIUS = 25;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), RADIUS, RADIUS);
                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.GRAY);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, RADIUS, RADIUS);
                g2.dispose();
            }
        };

        cardPanel.setOpaque(false);
        cardPanel.setLayout(null);
        cardPanel.setBackground(new Color(227, 227, 227));
        cardPanel.setBounds(600, 150, 450, 380);

        JLabel emailLabel = new JLabel("Email");
        emailLabel.setBounds(40, 15, 100, 20);
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailLabel.setForeground(new Color(90, 90, 90));

        emailField = new JTextField();
        emailField.setBounds(40, 40, 370, 55);
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setBounds(40, 95, 100, 20);
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordLabel.setForeground(new Color(90, 90, 90));

        passwordField = new JPasswordField();
        passwordField.setBounds(40, 120, 370, 55);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        JCheckBox showPassword = new JCheckBox("Show Password");
        showPassword.setBounds(305, 175, 130, 25);
        showPassword.setOpaque(false); 
        showPassword.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        showPassword.addActionListener(e -> {
            if (showPassword.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('•');
            }
        });
        cardPanel.add(showPassword);

        ((AbstractDocument) passwordField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                int currentLength = fb.getDocument().getLength();
                int overLimit = (currentLength + text.length()) - 50 - length;
                if (overLimit > 0) {
                    text = text.substring(0, text.length() - overLimit);
                    JOptionPane.showMessageDialog(LoginPanel.this,
                            "Password cannot exceed 50 characters.", "Warning",
                            JOptionPane.WARNING_MESSAGE);
                }
                if (!text.isEmpty()) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });

        JButton loginButton = new JButton("Log In");
        loginButton.setBounds(40, 200, 370, 50);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        loginButton.setBackground(new Color(0x35, 0x40, 0x8F));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.addActionListener(e -> loginUser());

        JSeparator separator = new JSeparator();
        separator.setBounds(40, 265, 370, 1);

        JButton registerButton = new JButton("Register");
        registerButton.setBounds(40, 290, 370, 50);
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        registerButton.setBackground(new Color(218,164,37));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.addActionListener(e -> frame.showRegister());

        cardPanel.add(emailField);
        cardPanel.add(emailLabel);
        cardPanel.add(passwordField);
        cardPanel.add(passwordLabel);
        cardPanel.add(loginButton);
        cardPanel.add(separator);
        cardPanel.add(registerButton);

        add(cardPanel);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //blue
        g2.setColor(new Color(0x35, 0x40, 0x8F));
        g2.fillRect(0, 0, getWidth(), getHeight());

        //White Triangle
        g2.setColor(new Color(218,164,37));
        int[] xPoints = {0, getWidth() / 1,0}; 
        int[] yPoints = {0, 0, getHeight()};
        g2.fillPolygon(xPoints, yPoints, 3);

        g2.dispose();
    }

    private void loginUser() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter email and password.",
                    "Login Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String result = Database.validateLogin(email, password);

        if ("PENDING".equals(result)) {
            JOptionPane.showMessageDialog(this,
                    "Your account is still pending admin approval.",
                    "Account Pending", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if ("INVALID".equals(result)) {
            JOptionPane.showMessageDialog(this, "Invalid email or password.",
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "Login successful!");

        if ("ADMIN".equals(result)) {
            new AdminDashboard(frame, email).setVisible(true);
        } else {
            new HomePage(email).setVisible(true);
        }
        frame.dispose();
    }
}
