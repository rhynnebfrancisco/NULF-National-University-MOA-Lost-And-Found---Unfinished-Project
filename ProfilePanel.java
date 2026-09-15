import java.awt.*;
import javax.swing.*;

public class ProfilePanel extends JPanel {

    public ProfilePanel(String userEmail) {
        setLayout(null);
        setBackground(Color.WHITE);
        setBounds(0, 0, 1150, 680);

        String[] userData = Database.getFullUserProfile(userEmail);

        String firstName = (userData != null && userData[0] != null) ? userData[0] : "";
        String lastName = (userData != null && userData[1] != null) ? userData[1] : "";
        String studentID = (userData != null && userData[2] != null) ? userData[2] : "N/A";
        String email = (userData != null && userData[3] != null) ? userData[3] : "N/A";
        String program = (userData != null && userData[4] != null) ? userData[4] : "N/A";
        String section = (userData != null && userData[5] != null) ? userData[5] : "N/A";
        String yearLevel = (userData != null && userData[6] != null) ? userData[6] : "";
        String middleInitial = (userData != null && userData[7] != null) ? userData[7] : "";
        String suffix = (userData != null && userData[8] != null) ? userData[8] : "";
        String gender = (userData != null && userData[9] != null) ? userData[9] : "N/A";
        String birthdate = (userData != null && userData[10] != null) ? userData[10] : "N/A";

        StringBuilder fullNameBuilder = new StringBuilder(firstName);

        if (middleInitial != null && !middleInitial.trim().isEmpty() && !middleInitial.equals("N/A")) {
            fullNameBuilder.append(" ").append(middleInitial).append(".");
        }

        fullNameBuilder.append(" ").append(lastName);

        if (suffix != null && !suffix.trim().isEmpty() && !suffix.equalsIgnoreCase("N/A")
                && !suffix.equalsIgnoreCase("NA")) {
            fullNameBuilder.append(" ").append(suffix);
        }

        String fullNameDisplay = fullNameBuilder.toString();

        JLabel titleLabel = new JLabel("My Profile");
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

        JLabel nameLabel = new JLabel(fullNameDisplay);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        nameLabel.setForeground(new Color(25, 42, 86));
        nameLabel.setBounds(50, 50, 600, 30);
        card.add(nameLabel);

        String roleText = (yearLevel.isEmpty()) ? "Student / User" : yearLevel + " Student";
        JLabel roleLabel = new JLabel(roleText);
        roleLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        roleLabel.setForeground(Color.GRAY);
        roleLabel.setBounds(50, 85, 300, 20);
        card.add(roleLabel);

        JSeparator sep = new JSeparator();
        sep.setBounds(50, 140, 700, 2);
        sep.setForeground(Color.LIGHT_GRAY);
        card.add(sep);

        addDetail(card, "Student ID Number:", studentID, 50, 170);
        addDetail(card, "Email Address:", email, 400, 170);

        addDetail(card, "Program:", program, 50, 250);
        addDetail(card, "Section:", section, 400, 250);

        addDetail(card, "Gender:", gender, 50, 330);
        addDetail(card, "Birthdate:", birthdate, 400, 330);

        add(card);
    }

    private void addDetail(JPanel panel, String label, String value, int x, int y) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        lbl.setForeground(new Color(84, 119, 146));
        lbl.setBounds(x, y, 300, 20);
        panel.add(lbl);

        JLabel val = new JLabel(value);
        val.setFont(new Font("SansSerif", Font.PLAIN, 16));
        val.setForeground(Color.DARK_GRAY);
        val.setBounds(x, y + 25, 300, 30);
        val.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        panel.add(val);
    }
}