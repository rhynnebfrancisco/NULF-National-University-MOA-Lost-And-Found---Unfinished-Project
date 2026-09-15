import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
        Database.initialize();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new AppFrame();
            }
        });
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("NULF");

        frame.setSize(1150, 680);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.getContentPane().setBackground(new Color(0x54, 0x77, 0x92));

        JPanel placeholderPanel = new JPanel();
        placeholderPanel.setBackground(new Color(0x54, 0x77, 0x92));
        placeholderPanel.setLayout(null);

        JLabel titleLabel = new JLabel("NULF");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        titleLabel.setBounds(100, 380, 800, 60);

        JLabel subtitleLabel = new JLabel("NULF");
        subtitleLabel.setForeground(Color.WHITE);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        subtitleLabel.setBounds(100, 440, 800, 40);

        placeholderPanel.add(titleLabel);
        placeholderPanel.add(subtitleLabel);

        frame.setContentPane(placeholderPanel);
        frame.setVisible(true);
    }
}