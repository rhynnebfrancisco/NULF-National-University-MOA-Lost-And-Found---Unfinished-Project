import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class RegisterPanel extends JPanel {

    private AppFrame frame;

    private JTextField firstNameField, lastNameField, middleInitialField, suffixField;
    private JTextField emailField, studentIdField, sectionField;
    private JPasswordField passwordField;
    private JCheckBox showPasswordCheck;
    private JComboBox<String> monthBox, dayBox, yearBox, yearLevelBox, programBox;
    private JRadioButton femaleRadio, maleRadio;
    private ButtonGroup genderGroup;

    public RegisterPanel(AppFrame frame) {

        this.frame = frame;

        Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("Logo-NULF.png"));
        frame.setIconImage(icon);

        setLayout(null);
        setBackground(new Color(0x35, 0x40, 0x8F));
        setBounds(0, 0, 1150, 700);

        JLabel titleLabel = new JLabel("Register", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setBounds(420, 15, 300, 40);
        add(titleLabel);

        JPanel cardPanel = new JPanel() {
            private static final int RADIUS = 25;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setClip(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), RADIUS, RADIUS));
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), RADIUS, RADIUS);
                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(0, 0, 0, 40));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, RADIUS, RADIUS);
                g2.dispose();
            }
        };

        cardPanel.setOpaque(false);
        cardPanel.setLayout(null);
        cardPanel.setBackground(new Color(204, 210, 214));

        int cardWidth = 480;
        int cardHeight = 570;
        int cardX = (1150 - cardWidth) / 2;
        int cardY = (680 - cardHeight) / 2;
        cardPanel.setBounds(cardX, cardY, cardWidth, cardHeight);

        JLabel cardTitle = new JLabel("Create an account", SwingConstants.CENTER);
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        cardTitle.setForeground(new Color(0x4A, 0x70, 0xA9));
        cardTitle.setBounds(0, 10, cardWidth, 30);

        JLabel subtitle = new JLabel("It's quick and easy.", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(Color.GRAY);
        subtitle.setBounds(0, 35, cardWidth, 20);

        JLabel fnLabel = createFieldLabel("First Name", 30, 60, 200, 15);
        firstNameField = createCleanField("", 30, 75, 200, false);

        JLabel lnLabel = createFieldLabel("Last Name", 250, 60, 200, 15);
        lastNameField = createCleanField("", 250, 75, 200, false);

        JLabel miLabel = createFieldLabel("Middle Initial", 30, 115, 200, 15);
        middleInitialField = createCleanField("", 30, 130, 200, true);

        ((AbstractDocument) middleInitialField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr)
                    throws BadLocationException {
                if (text != null && fb.getDocument().getLength() < 1) {
                    super.insertString(fb, offset, text.toUpperCase().substring(0, 1), attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                if (text != null) {
                    String newText = text.toUpperCase();
                    if (fb.getDocument().getLength() - length + newText.length() <= 1) {
                        super.replace(fb, offset, length, newText, attrs);
                    }
                }
            }
        });

        JLabel suffixLabel = createFieldLabel("Suffix (Optional)", 250, 115, 200, 15);
        suffixField = createCleanField("", 250, 130, 200, true);

        JLabel birthLabel = createFieldLabel("Birthdate", 30, 170, 320, 15);

        monthBox = new JComboBox<>(new String[]{
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        });
        styleComboBox(monthBox);
        monthBox.setBounds(30, 185, 120, 35);

        dayBox = new JComboBox<>();
        for (int i = 1; i <= 31; i++) dayBox.addItem(String.valueOf(i));
        styleComboBox(dayBox);
        dayBox.setBounds(160, 185, 90, 35);

        yearBox = new JComboBox<>();
        for (int y = 2026; y >= 1900; y--) yearBox.addItem(String.valueOf(y));
        styleComboBox(yearBox);
        yearBox.setBounds(260, 185, 90, 35);

        yearBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                int selectedYear = Integer.parseInt((String) yearBox.getSelectedItem());
                if (selectedYear <= 1980) {
                    JOptionPane.showMessageDialog(
                            null,
                            "Only birth years 1980 and later are allowed.",
                            "Invalid Year",
                            JOptionPane.ERROR_MESSAGE
                    );
                    yearBox.setSelectedItem("1980");
                }
            }
        });

        JLabel genderLabel = createFieldLabel("Gender", 30, 220, 200, 15);

        femaleRadio = new JRadioButton("Female");
        styleRadioButton(femaleRadio);
        femaleRadio.setBounds(30, 235, 100, 25);
        femaleRadio.setBackground(Color.WHITE);
        femaleRadio.setForeground(Color.DARK_GRAY);
        Color radioBg = new Color(240, 240, 240);
        femaleRadio.setBackground(radioBg);

        maleRadio = new JRadioButton("Male");
        styleRadioButton(maleRadio);
        maleRadio.setBounds(150, 235, 100, 25);
        maleRadio.setBackground(Color.WHITE);
        maleRadio.setForeground(Color.DARK_GRAY);
        maleRadio.setBackground(radioBg);

        genderGroup = new ButtonGroup();
        genderGroup.add(femaleRadio);
        genderGroup.add(maleRadio);

        JLabel emailLabel = createFieldLabel("Email", 30, 265, 420, 15);
        emailField = createCleanField("", 30, 280, 420, false);

        JLabel passLabel = createFieldLabel("Password", 30, 320, 420, 15);
        passwordField = new JPasswordField();
        passwordField.setBounds(30, 335, 420, 40);
        stylePasswordField(passwordField);

        ((AbstractDocument) passwordField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                if (text != null && fb.getDocument().getLength() + text.length() <= 12) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });

        showPasswordCheck = new JCheckBox("Show Password");
        showPasswordCheck.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        showPasswordCheck.setForeground(Color.DARK_GRAY);
        showPasswordCheck.setOpaque(false);
        showPasswordCheck.setBounds(345, 370, 110, 25);

        char defaultEcho = passwordField.getEchoChar();

        showPasswordCheck.addActionListener(e -> {
            if (showPasswordCheck.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar(defaultEcho);
            }
        });

        JLabel sidLabel = createFieldLabel("Student ID (YYYY-XXXXXXX)", 30, 375, 420, 15);
        studentIdField = createCleanField("", 30, 390, 420, false);

        JLabel yearLevelLabel = createFieldLabel("Year Level", 30, 430, 100, 15);
        yearLevelBox = new JComboBox<>(new String[]{
                "1st Year", "2nd Year", "3rd Year", "4th Year", "5th Year+"
        });
        styleComboBox(yearLevelBox);
        yearLevelBox.setBounds(30, 445, 100, 40);

        JLabel programLabel = createFieldLabel("Program", 135, 430, 160, 15);
        programBox = new JComboBox<>(new String[]{
                "Doctor of Dental Medicine",
                "Dental Hygiene Level IV",
                "Dental Technology NCIV",
                "Doctor of Optometry",
                "BS Psych",
                "BS MedTech",
                "BS Arch",
                "BSA",
                "BSBA",
                "BSTM",
                "BSIT (Mobile/Web)",
                "ABM",
                "HUMSS",
                "STEM"
        });

        styleComboBox(programBox);
        programBox.setBounds(135, 445, 160, 40);
        programBox.setSelectedIndex(-1);
        programBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                         boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value == null && index == -1) {
                    setText("Select Program");
                    setForeground(Color.GRAY);
                }

                return this;
            }
        });

        JLabel sectionLabel = createFieldLabel("Section", 300, 430, 150, 15);
        sectionField = createCleanField("", 300, 445, 150, true);

        JButton registerButton = new JButton("Register");
        registerButton.setBounds(145, 495, 200, 40);
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        registerButton.setBackground(new Color(218,164,37)); 
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.addActionListener(e -> registerUser());
        
        JLabel loginLink = new JLabel("Already have an account?", SwingConstants.CENTER);
        loginLink.setBounds(140, 540, 200, 25);
        loginLink.setForeground(new Color(0x4A, 0x70, 0xA9));
        loginLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                frame.showLogin();
            }
        });

        cardPanel.add(cardTitle);
        cardPanel.add(subtitle);
        cardPanel.add(fnLabel);
        cardPanel.add(firstNameField);
        cardPanel.add(lnLabel);
        cardPanel.add(lastNameField);
        cardPanel.add(miLabel);
        cardPanel.add(middleInitialField);
        cardPanel.add(suffixLabel);
        cardPanel.add(suffixField);
        cardPanel.add(birthLabel);
        cardPanel.add(monthBox);
        cardPanel.add(dayBox);
        cardPanel.add(yearBox);
        cardPanel.add(genderLabel);
        cardPanel.add(femaleRadio);
        cardPanel.add(maleRadio);
        cardPanel.add(emailLabel);
        cardPanel.add(emailField);
        cardPanel.add(passLabel);
        cardPanel.add(passwordField);
        cardPanel.add(showPasswordCheck);
        cardPanel.add(sidLabel);
        cardPanel.add(studentIdField);
        cardPanel.add(yearLevelLabel);
        cardPanel.add(yearLevelBox);
        cardPanel.add(programLabel);
        cardPanel.add(programBox);
        cardPanel.add(sectionLabel);
        cardPanel.add(sectionField);
        cardPanel.add(registerButton);
        cardPanel.add(loginLink);

        add(cardPanel);
    }

    private JLabel createFieldLabel(String text, int x, int y, int width, int height) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, width, height);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(Color.DARK_GRAY);
        return label;
    }

    private JTextField createCleanField(String text, int x, int y, int width, boolean uppercase) {
        JTextField field = new JTextField(text);
        field.setBounds(x, y, width, 40);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(Color.WHITE);
        field.setForeground(Color.DARK_GRAY);
        field.setBorder(BorderFactory.createLineBorder(new Color(0xC0C0C0)));

        if (uppercase) {
            ((AbstractDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
                @Override
                public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr)
                        throws BadLocationException {
                    if (text != null)
                        super.insertString(fb, offset, text.toUpperCase(), attr);
                }

                @Override
                public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                        throws BadLocationException {
                    if (text != null)
                        super.replace(fb, offset, length, text.toUpperCase(), attrs);
                }
            });
        }

        return field;
    }

    private void stylePasswordField(JPasswordField field) {
        field.setBackground(Color.WHITE);
        field.setForeground(Color.DARK_GRAY);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createLineBorder(new Color(0xC0C0C0)));
    }

    private void styleComboBox(JComboBox<String> combo) {
        combo.setBackground(Color.WHITE);
        combo.setForeground(Color.DARK_GRAY);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setBorder(BorderFactory.createLineBorder(new Color(0xC0C0C0)));
    }

    private void styleRadioButton(JRadioButton radio) {
        radio.setBackground(new Color(204, 210, 214));
        radio.setForeground(Color.DARK_GRAY);
        radio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }

    private void registerUser() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String middleInitial = middleInitialField.getText().trim();
        String suffix = suffixField.getText().trim();

        String birthdate = monthBox.getSelectedItem() + " "
                + dayBox.getSelectedItem() + ", "
                + yearBox.getSelectedItem();

        String gender = femaleRadio.isSelected() ? "Female"
                : (maleRadio.isSelected() ? "Male" : "");

        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String studentId = studentIdField.getText().trim();
        String yearLevel = (String) yearLevelBox.getSelectedItem();

        String program = (String) programBox.getSelectedItem();
        String section = sectionField.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()
        || password.isEmpty() || studentId.isEmpty()
        || gender.isEmpty() || program == null || section.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please fill in all required fields.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        if (!firstName.matches("[a-zA-Z ]+")) {
            JOptionPane.showMessageDialog(
                    this,
                    "First Name must not contain numbers or special characters.",
                    "Invalid First Name",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        if (!lastName.matches("[a-zA-Z ]+")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Last Name must not contain numbers or special characters.",
                    "Invalid Last Name",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        if (!email.toLowerCase().endsWith("@students.nu-moa.edu.ph")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid school email ending with @students.nu-moa.edu.ph.",
                    "Invalid Email",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()
                || password.isEmpty() || studentId.isEmpty()
                || gender.isEmpty() || program == null || section.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please fill in all required fields.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (!email.toLowerCase().endsWith("@students.nu-moa.edu.ph")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid school email ending with @students.nu-moa.edu.ph.",
                    "Invalid Email",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (!studentId.matches("\\d{4}-\\d{1,7}")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Student ID must follow format: YYYY-XXXXXXX (e.g. 2025-1234567)",
                    "Invalid Student ID",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        boolean success = Database.registerUser(
                firstName, lastName, middleInitial, suffix,
                birthdate, gender, email, password,
                studentId, yearLevel, program, section
        );

        if (success) {
            JOptionPane.showMessageDialog(
                    this,
                    "Registration submitted.\nWaiting for admin approval.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );
            frame.showLogin();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Registration failed. Please try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
