import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

final class PlacementSplash {
    private PlacementSplash() {
    }

    static void show() {
        JWindow splash = new JWindow();
        splash.setSize(540, 340);
        splash.setLocationRelativeTo(null);

        JPanel background = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, new Color(13, 17, 23),
                        getWidth(), getHeight(), new Color(30, 27, 60)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(99, 102, 241, 30));
                g2.fillOval(380, -60, 220, 220);
                g2.setColor(new Color(99, 102, 241, 15));
                g2.fillOval(340, -30, 280, 280);
            }
        };
        background.setBorder(new EmptyBorder(40, 50, 36, 50));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel icon = new JLabel("CP", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI", Font.BOLD, 42));
        icon.setForeground(new Color(248, 250, 252));
        icon.setOpaque(true);
        icon.setBackground(new Color(99, 102, 241));
        icon.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(129, 140, 248), 1, true),
                new EmptyBorder(8, 18, 8, 18)));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Campus Placement Management System", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 19));
        title.setForeground(new Color(248, 250, 252));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel college = new JLabel("St. Joseph's College of Engineering", SwingConstants.CENTER);
        college.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        college.setForeground(new Color(99, 102, 241));
        college.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel developer = new JLabel("Karthikeyan C K  ·  25IT347", SwingConstants.CENTER);
        developer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        developer.setForeground(new Color(100, 116, 139));
        developer.setAlignmentX(Component.CENTER_ALIGNMENT);

        JProgressBar progress = new JProgressBar(0, 100);
        progress.setStringPainted(false);
        progress.setBackground(new Color(30, 30, 50));
        progress.setForeground(new Color(99, 102, 241));
        progress.setBorderPainted(false);
        progress.setPreferredSize(new Dimension(400, 4));
        progress.setMaximumSize(new Dimension(Integer.MAX_VALUE, 4));
        progress.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel loadingLabel = new JLabel("Initializing...", SwingConstants.CENTER);
        loadingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        loadingLabel.setForeground(new Color(100, 116, 139));
        loadingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        content.add(icon);
        content.add(Box.createVerticalStrut(14));
        content.add(title);
        content.add(Box.createVerticalStrut(6));
        content.add(college);
        content.add(Box.createVerticalStrut(4));
        content.add(developer);
        content.add(Box.createVerticalStrut(28));
        content.add(progress);
        content.add(Box.createVerticalStrut(10));
        content.add(loadingLabel);

        background.add(content);
        splash.add(background);
        splash.setVisible(true);

        String[] messages = {"Initializing UI components...", "Connecting to database...",
                "Loading placement records...", "Applying styles...", "Almost ready!"};
        int[] progressValue = {0};
        Timer timer = new Timer(20, null);
        timer.addActionListener(e -> {
            progressValue[0] += 2;
            progress.setValue(progressValue[0]);
            loadingLabel.setText(messages[Math.min(progressValue[0] / 20, messages.length - 1)]);
            if (progressValue[0] >= 100) {
                timer.stop();
                splash.dispose();
                CampusPlacementSystem app = new CampusPlacementSystem();
                app.setVisible(true);
            }
        });
        timer.start();
    }
}
