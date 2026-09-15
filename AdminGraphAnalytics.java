import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AdminGraphAnalytics extends JPanel {

    private static final String DB_URL = "jdbc:sqlite:lostandfound.db";

    private int[] values;
    private final String[] labels = {
            "Users",
            "Lost Items",
            "Found Items",
            "Approved Claims",
            "Pending Claims"
    };

    private BarChartPanel chartPanel;

    public AdminGraphAnalytics() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Graph Analytics", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        add(title, BorderLayout.NORTH);

        chartPanel = new BarChartPanel();
        add(chartPanel, BorderLayout.CENTER);

        refreshData(); // initial load
    }

    // 🔄 CALL THIS TO REFRESH DATA
    public void refreshData() {
        values = new int[]{
                getCount("SELECT COUNT(*) FROM users"),
                getCount("SELECT COUNT(*) FROM items WHERE status = 'LOST'"),
                getCount("SELECT COUNT(*) FROM items WHERE status = 'FOUND'"),
                getCount("SELECT COUNT(*) FROM claims WHERE status = 'APPROVED'"),
                getCount("SELECT COUNT(*) FROM claims WHERE status = 'PENDING'")
        };
        chartPanel.repaint();
    }

    private int getCount(String sql) {
        try (Connection con = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ================= BAR CHART =================

    class BarChartPanel extends JPanel {

        BarChartPanel() {
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (values == null) return;

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int height = getHeight();
            int padding = 70;
            int barWidth = 80;
            int gap = 40;

            int maxValue = getMaxValue();
            int x = padding;

            for (int i = 0; i < values.length; i++) {
                int barHeight = (int) ((values[i] / (double) maxValue) * (height - 200));
                int y = height - barHeight - padding;

                g2.setColor(new Color(25, 42, 86));
                g2.fillRoundRect(x, y, barWidth, barHeight, 12, 12);

                g2.setColor(Color.BLACK);
                g2.setFont(new Font("SansSerif", Font.BOLD, 12));
                g2.drawString(String.valueOf(values[i]), x + 30, y - 5);

                g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
                g2.drawString(labels[i], x + 5, height - 30);

                x += barWidth + gap;
            }
        }

        private int getMaxValue() {
            int max = values[0];
            for (int v : values) {
                if (v > max) max = v;
            }
            return max == 0 ? 1 : max;
        }
    }
}
