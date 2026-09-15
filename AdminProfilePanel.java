import java.awt.*;
import javax.swing.*;

public class AdminProfilePanel extends JPanel {

    private JTextField firstNameField, lastNameField, emailField;
    private JPasswordField passwordField;
    private String currentUserEmail;

    private Runnable refreshCallback;

    public AdminProfilePanel() {

        this("", null);
    }

    public AdminProfilePanel(String userEmail) {
        this(userEmail, null);
    }

    public AdminProfilePanel(String userEmail, Runnable onUpdateSuccess) {
        this.currentUserEmail = userEmail;
        this.refreshCallback = onUpdateSuccess;

        setLayout(null);
        setBackground(Color.WHITE);
        setBounds(0, 0, 1150, 680);

        String[] userData = Database.getFullUserProfile(userEmail);

        String firstName = (userData != null && userData[0] != null) ? userData[0] : "";
        String lastName = (userData != null && userData[1] != null) ? userData[1] : "";
        String email = (userData != null && userData[3] != null) ? userData[3] : userEmail;
        String role = (userData != null && userData.length > 11) ? userData[11] : "ADMIN";

        JLabel titleLabel = new JLabel("Admin Profile");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
        titleLabel.setForeground(new Color(25, 42, 86));
        titleLabel.setBounds(50, 30, 300, 40);
        add(titleLabel);

        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(245, 248, 250));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
            }
        };
        card.setLayout(null);
        card.setBounds(50, 90, 800, 500);

        JLabel nameLabel = new JLabel(firstName + " " + lastName);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        nameLabel.setForeground(new Color(25, 42, 86));
        nameLabel.setBounds(50, 50, 600, 30);
        card.add(nameLabel);

        JLabel roleLabel = new JLabel(role + " ACCOUNT");
        roleLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        roleLabel.setForeground(Color.GRAY);
        roleLabel.setBounds(50, 85, 300, 20);
        card.add(roleLabel);

        JSeparator sep = new JSeparator();
        sep.setBounds(50, 140, 700, 2);
        sep.setForeground(Color.LIGHT_GRAY);
        card.add(sep);

        int startY = 170;
        int gapY = 80;

        firstNameField = createLabeledField(card, "First Name:", firstName, 50, startY, 300);
        lastNameField = createLabeledField(card, "Last Name:", lastName, 400, startY, 300);

        emailField = createLabeledField(card, "Email Address:", email, 50, startY + gapY, 300);

        JLabel passLabel = new JLabel("New Password (Optional):");
        passLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        passLabel.setForeground(new Color(84, 119, 146));
        passLabel.setBounds(400, startY + gapY, 300, 20);
        card.add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(400, startY + gapY + 25, 300, 40);
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        card.add(passwordField);

        JButton saveBtn = new JButton("Save Changes");
        saveBtn.setBounds(50, 400, 200, 45);
        saveBtn.setBackground(new Color(46, 204, 113));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        saveBtn.setFocusPainted(false);
        saveBtn.addActionListener(e -> saveChanges(nameLabel));
        card.add(saveBtn);

        add(card);
    }

    private JTextField createLabeledField(JPanel panel, String label, String value, int x, int y, int width) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        lbl.setForeground(new Color(84, 119, 146));
        lbl.setBounds(x, y, width, 20);
        panel.add(lbl);

        JTextField field = new JTextField(value);
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBounds(x, y + 25, width, 40);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        panel.add(field);
        return field;
    }

    private void saveChanges(JLabel nameLabelToUpdate) {
        String fName = firstNameField.getText().trim();
        String lName = lastNameField.getText().trim();
        String newEmail = emailField.getText().trim();
        String newPass = new String(passwordField.getPassword()).trim();

        if (fName.isEmpty() || lName.isEmpty() || newEmail.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and Email cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] currentData = Database.getFullUserProfile(currentUserEmail);
        String id = (currentData != null) ? currentData[2] : "ADMIN-001";
        String prog = (currentData != null) ? currentData[4] : "Admin Dept";
        String sec = (currentData != null) ? currentData[5] : "N/A";
        String year = (currentData != null) ? currentData[6] : "N/A";
        String mi = (currentData != null) ? currentData[7] : "";
        String suf = (currentData != null) ? currentData[8] : "";
        String gen = (currentData != null) ? currentData[9] : "N/A";
        String role = (currentData != null && currentData.length > 11) ? currentData[11] : "ADMIN";

        boolean basicSuccess = Database.updateUser(
                currentUserEmail, fName, lName, mi, suf, newEmail, id, prog, sec, year, gen, role);

        boolean passSuccess = true;
        if (!newPass.isEmpty()) {

            passSuccess = Database.updateUserPassword(newEmail, newPass);
        }

        if (basicSuccess && passSuccess) {
            String msg = "Profile updated successfully!";
            if (!newPass.isEmpty())
                msg += "\nPassword has been changed.";

            JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);

            nameLabelToUpdate.setText(fName + " " + lName);
            this.currentUserEmail = newEmail;
            passwordField.setText("");

            if (refreshCallback != null) {
                refreshCallback.run();
            }

        } else {
            JOptionPane.showMessageDialog(this, "Failed to update profile. Please try again.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}