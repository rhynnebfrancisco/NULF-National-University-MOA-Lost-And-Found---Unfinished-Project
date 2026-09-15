import java.awt.*;
import javax.swing.*;

public class AdminUpdateUser extends JPanel {

    private JTextField firstNameField, lastNameField, middleInitialField, suffixField;
    private JTextField studentIdField, emailField, programField, passwordField, sectionField;
    private JComboBox<String> yearLevelBox;
    private JComboBox<String> roleBox;
    private JRadioButton femaleRadio, maleRadio;
    private String originalEmail;

    private JPanel parentContentArea;
    private CardLayout parentLayout;
    private AdminUserManagement managePanel;

    public AdminUpdateUser(String name, String studentId, String email, String program, String yearLevel,
            String section,
            JPanel contentArea, CardLayout layout, AdminUserManagement managePanel) {
        this.originalEmail = email;
        this.parentContentArea = contentArea;
        this.parentLayout = layout;
        this.managePanel = managePanel;

        setLayout(null);
        setBackground(new Color(245, 248, 250));

        JPanel headerPanel = new JPanel();
        headerPanel.setBounds(0, 0, 1150, 60);
        headerPanel.setBackground(new Color(25, 42, 86));
        headerPanel.setLayout(new GridBagLayout());

        JLabel titleLabel = new JLabel("UPDATE USER DETAILS");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        add(headerPanel);

        String[] fullData = Database.getFullUserProfile(email);

        String fName = (fullData != null) ? fullData[0] : "";
        String lName = (fullData != null) ? fullData[1] : "";
        String mi = (fullData != null) ? fullData[7] : "";
        String suf = (fullData != null) ? fullData[8] : "";
        String gender = (fullData != null) ? fullData[9] : "";
        String currentSection = (fullData != null) ? fullData[5] : "";
        String role = (fullData != null && fullData.length > 11) ? fullData[11] : "USER";

        int startY = 80;
        int rowHeight = 70;
        int leftColX = 100;
        int rightColX = 480;
        int fieldWidth = 350;

        firstNameField = createLabeledField("First Name:", fName, leftColX, startY, 240);
        middleInitialField = createLabeledField("M.I.:", mi, leftColX + 260, startY, 80);

        lastNameField = createLabeledField("Last Name:", lName, rightColX, startY, 240);
        suffixField = createLabeledField("Suffix:", suf, rightColX + 260, startY, 80);

        studentIdField = createLabeledField("Student Number:", studentId, leftColX, startY + rowHeight, fieldWidth);
        emailField = createLabeledField("Email Address:", email, rightColX, startY + rowHeight, fieldWidth);

        programField = createLabeledField("Department:", program, leftColX, startY + rowHeight * 2, fieldWidth);
        sectionField = createLabeledField("Section:", currentSection, rightColX, startY + rowHeight * 2, fieldWidth);

        JLabel yearLabel = new JLabel("Year Level:");
        yearLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        yearLabel.setForeground(Color.DARK_GRAY);
        yearLabel.setBounds(leftColX, startY + rowHeight * 3, 150, 20);
        add(yearLabel);

        yearLevelBox = new JComboBox<>(new String[] { "1st Year", "2nd Year", "3rd Year", "4th Year", "5th Year+" });
        yearLevelBox.setSelectedItem(yearLevel);
        yearLevelBox.setBounds(leftColX, startY + rowHeight * 3 + 25, fieldWidth, 40);
        yearLevelBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        yearLevelBox.setBackground(Color.WHITE);
        add(yearLevelBox);

        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        roleLabel.setForeground(Color.DARK_GRAY);
        roleLabel.setBounds(rightColX, startY + rowHeight * 3, 150, 20);
        add(roleLabel);

        roleBox = new JComboBox<>(new String[] { "USER", "ADMIN" });
        roleBox.setSelectedItem(role);
        roleBox.setBounds(rightColX, startY + rowHeight * 3 + 25, fieldWidth, 40);
        roleBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        roleBox.setBackground(Color.WHITE);
        add(roleBox);

        JLabel genderLabel = new JLabel("Gender:");
        genderLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        genderLabel.setForeground(Color.DARK_GRAY);
        genderLabel.setBounds(leftColX, startY + rowHeight * 4, 100, 20);
        add(genderLabel);

        femaleRadio = new JRadioButton("Female");
        femaleRadio.setBounds(leftColX, startY + rowHeight * 4 + 25, 80, 40);
        femaleRadio.setBackground(new Color(245, 248, 250));

        maleRadio = new JRadioButton("Male");
        maleRadio.setBounds(leftColX + 100, startY + rowHeight * 4 + 25, 80, 40);
        maleRadio.setBackground(new Color(245, 248, 250));

        ButtonGroup bg = new ButtonGroup();
        bg.add(femaleRadio);
        bg.add(maleRadio);

        if ("Male".equalsIgnoreCase(gender))
            maleRadio.setSelected(true);
        else
            femaleRadio.setSelected(true);

        add(femaleRadio);
        add(maleRadio);

        passwordField = createLabeledField("New Password", "", rightColX, startY + rowHeight * 4, fieldWidth);

        int btnY = startY + rowHeight * 5 + 30;

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBounds(leftColX + 150, btnY, 180, 45);
        cancelBtn.setBackground(new Color(231, 76, 60));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        cancelBtn.setFocusPainted(false);
        cancelBtn.addActionListener(e -> goBack());
        add(cancelBtn);

        JButton updateBtn = new JButton("Update User");
        updateBtn.setBounds(rightColX, btnY, 180, 45);
        updateBtn.setBackground(new Color(25, 42, 86));
        updateBtn.setForeground(Color.WHITE);
        updateBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        updateBtn.setFocusPainted(false);
        updateBtn.addActionListener(e -> updateUser());
        add(updateBtn);
    }

    private JTextField createLabeledField(String labelText, String value, int x, int y, int width) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setForeground(Color.DARK_GRAY);
        label.setBounds(x, y, width, 20);
        add(label);

        JTextField field = new JTextField(value);
        field.setBounds(x, y + 25, width, 40);
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        add(field);
        return field;
    }

    private void goBack() {

        parentLayout.show(parentContentArea, "VIEW_USERS");
    }

    private void updateUser() {
        String fName = firstNameField.getText().trim();
        String lName = lastNameField.getText().trim();
        String mi = middleInitialField.getText().trim();
        String suf = suffixField.getText().trim();
        String newId = studentIdField.getText().trim();
        String newEmail = emailField.getText().trim();
        String newProgram = programField.getText().trim();
        String newSection = sectionField.getText().trim();
        String newYear = (String) yearLevelBox.getSelectedItem();
        String newRole = (String) roleBox.getSelectedItem();
        String newGender = maleRadio.isSelected() ? "Male" : "Female";

        if (fName.isEmpty() || lName.isEmpty() || newId.isEmpty() || newEmail.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Required fields cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = Database.updateUser(
                originalEmail, fName, lName, mi, suf,
                newEmail, newId, newProgram, newSection, newYear, newGender, newRole);

        if (success) {
            JOptionPane.showMessageDialog(this, "User updated successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            if (managePanel != null) {
                managePanel.loadUserData();
            }
            goBack();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update user. Check database.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}