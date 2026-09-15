import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ReportMissingItemPanel extends JPanel {

    private JTextField itemNameField;
    private JTextField landmarkField;
    private JSpinner dateSpinner;
    private JSpinner timeSpinner;
    private JTextArea descriptionArea;
    private JLabel imagePreviewLabel;
    private String selectedImagePath = null;
    private String currentUserEmail;

    public ReportMissingItemPanel(String userEmail) {
        this.currentUserEmail = userEmail;

        setLayout(null);
        setBackground(Color.WHITE);
        setBounds(0, 0, 1150, 680);

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBounds(0, 0, 1150, 60);
        headerPanel.setBackground(new Color(240, 240, 240));
        JLabel header = new JLabel("REPORT MISSING ITEMS");
        header.setFont(new Font("SansSerif", Font.BOLD, 26));
        header.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 0));
        headerPanel.add(header);
        add(headerPanel);

        int startY = 90;
        int leftX = 50;
        int labelWidth = 200;
        int fieldWidth = 400;
        int gap = 60;

        addLabel("Item Name:", leftX, startY);
        itemNameField = createTextField(leftX, startY + 25, fieldWidth);
        add(itemNameField);

        addLabel("Landmark:", leftX, startY + gap);
        landmarkField = createTextField(leftX, startY + gap + 25, fieldWidth);
        add(landmarkField);

        addLabel("Date Found:", leftX, startY + gap * 2);
        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "MM/dd/yyyy");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setBounds(leftX, startY + gap * 2 + 25, fieldWidth, 40);
        dateSpinner.setFont(new Font("SansSerif", Font.PLAIN, 14));
        add(dateSpinner);

        addLabel("Time Found:", leftX, startY + gap * 3);
        timeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "hh:mm a");
        timeSpinner.setEditor(timeEditor);
        timeSpinner.setBounds(leftX, startY + gap * 3 + 25, fieldWidth, 40);
        timeSpinner.setFont(new Font("SansSerif", Font.PLAIN, 14));
        add(timeSpinner);

        addLabel("Description (separate with comma):", leftX, startY + gap * 4);
        descriptionArea = new JTextArea();
        descriptionArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setBounds(leftX, startY + gap * 4 + 25, fieldWidth, 80);
        add(descScroll);

        int rightX = 550;

        JLabel imgLabel = new JLabel("Insert Image (JPG/PNG only):");
        imgLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        imgLabel.setForeground(Color.DARK_GRAY);
        imgLabel.setBounds(rightX, startY, 300, 20);
        add(imgLabel);

        imagePreviewLabel = new JLabel("No Image Selected", SwingConstants.CENTER);
        imagePreviewLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        imagePreviewLabel.setBounds(rightX, startY + 25, 300, 200);
        add(imagePreviewLabel);

        JButton uploadBtn = new JButton("Select Image");
        uploadBtn.setBounds(rightX, startY + 235, 300, 40);
        uploadBtn.setBackground(new Color(25, 42, 86));
        uploadBtn.setForeground(Color.WHITE);
        uploadBtn.setFocusPainted(false);
        uploadBtn.addActionListener(e -> selectImage());
        add(uploadBtn);

        JButton submitBtn = new JButton("Submit Report");
        submitBtn.setBounds(leftX, 550, 200, 50);
        submitBtn.setBackground(new Color(46, 204, 113));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        submitBtn.setFocusPainted(false);
        submitBtn.addActionListener(e -> submitReport());
        add(submitBtn);
    }

    private void addLabel(String text, int x, int y) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setForeground(Color.DARK_GRAY);
        label.setBounds(x, y, 400, 20);
        add(label);
    }

    private JTextField createTextField(int x, int y, int width) {
        JTextField field = new JTextField();
        field.setBounds(x, y, width, 40);
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        return field;
    }

    private void selectImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", "jpg", "png", "jpeg"));
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            selectedImagePath = selectedFile.getAbsolutePath();

            ImageIcon icon = new ImageIcon(selectedImagePath);
            Image img = icon.getImage().getScaledInstance(300, 200, Image.SCALE_SMOOTH);
            imagePreviewLabel.setText("");
            imagePreviewLabel.setIcon(new ImageIcon(img));
        }
    }

    private void submitReport() {
        String itemName = itemNameField.getText().trim();
        String landmark = landmarkField.getText().trim();
        String desc = descriptionArea.getText().trim();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateFound = dateFormat.format(dateSpinner.getValue());

        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        String timeFound = timeFormat.format(timeSpinner.getValue());

        if (itemName.isEmpty() || landmark.isEmpty() || desc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all text fields.", "Missing Info",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean success = Database.reportItem(currentUserEmail, itemName, landmark, dateFound, timeFound, desc,
                selectedImagePath);

        if (success) {
            JOptionPane.showMessageDialog(this, "Item reported successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            itemNameField.setText("");
            landmarkField.setText("");
            descriptionArea.setText("");
            imagePreviewLabel.setIcon(null);
            imagePreviewLabel.setText("No Image Selected");
            selectedImagePath = null;
        } else {
            JOptionPane.showMessageDialog(this, "Failed to report item.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}