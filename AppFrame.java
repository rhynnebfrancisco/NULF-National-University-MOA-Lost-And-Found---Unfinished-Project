import javax.swing.*;
import java.awt.*;

public class AppFrame extends JFrame {

    private LoginPanel loginPanel;
    private RegisterPanel registerPanel;

    public AppFrame() {

        Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/Logo-NULF.png"));
        setIconImage(icon);

        setTitle("NULF");
        setSize(1150, 680);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        getContentPane().setBackground(new Color(0x54, 0x77, 0x92));
        setLayout(null);

        loginPanel = new LoginPanel(this);
        registerPanel = new RegisterPanel(this);

        add(loginPanel);
        add(registerPanel);

        showLogin();
        setVisible(true);
    }

    public void showLogin() {
        loginPanel.setVisible(true);
        registerPanel.setVisible(false);
    }

    public void showRegister() {
        loginPanel.setVisible(false);
        registerPanel.setVisible(true);
    }
}
