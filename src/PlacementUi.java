import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

final class PlacementUi {
    private PlacementUi() {
    }

    static void styleTable(JTable table) {
        table.setFont(CampusPlacementSystem.F_BODY);
        table.setRowHeight(38);
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(238, 240, 244));
        table.setSelectionBackground(CampusPlacementSystem.C_ROW_SEL);
        table.setSelectionForeground(CampusPlacementSystem.C_DARK_TXT);
        table.setBackground(CampusPlacementSystem.C_SURFACE);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setReorderingAllowed(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(CampusPlacementSystem.C_HDR);
        header.setForeground(CampusPlacementSystem.C_WHITE);
        header.setPreferredSize(new Dimension(0, 44));
        header.setBorder(BorderFactory.createMatteBorder(
                0, 0, 2, 0, CampusPlacementSystem.C_INDIGO));

        int[] widths = {42, 152, 88, 138, 120, 100, 110};
        for (int i = 0; i < widths.length && i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable source, Object value, boolean selected, boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(
                        source, value, selected, hasFocus, row, column);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                setHorizontalAlignment(column == 6 ? SwingConstants.RIGHT : SwingConstants.LEFT);
                if (!selected) {
                    setBackground(row % 2 == 0
                            ? CampusPlacementSystem.C_SURFACE : CampusPlacementSystem.C_ROW_ALT);
                    setForeground(CampusPlacementSystem.C_DARK_TXT);
                    if (column == 5 && value != null) {
                        String status = value.toString();
                        if ("Placed".equalsIgnoreCase(status)) {
                            setForeground(CampusPlacementSystem.C_GREEN2);
                        } else if ("Not Placed".equalsIgnoreCase(status)) {
                            setForeground(CampusPlacementSystem.C_RED);
                        } else if ("In Process".equalsIgnoreCase(status)) {
                            setForeground(CampusPlacementSystem.C_AMBER);
                        } else {
                            setForeground(CampusPlacementSystem.C_MUTED);
                        }
                        setFont(new Font("Segoe UI", Font.BOLD, 12));
                    } else {
                        setFont(CampusPlacementSystem.F_BODY);
                    }
                }
                return component;
            }
        });
        table.setDefaultRenderer(Double.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable source, Object value, boolean selected, boolean hasFocus, int row, int column) {
                if (value instanceof Double) value = String.format("%.2f", value);
                Component component = super.getTableCellRendererComponent(
                        source, value, selected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.RIGHT);
                setBorder(new EmptyBorder(0, 10, 0, 14));
                if (!selected) {
                    setBackground(row % 2 == 0
                            ? CampusPlacementSystem.C_SURFACE : CampusPlacementSystem.C_ROW_ALT);
                }
                return component;
            }
        });
        table.setDefaultRenderer(Integer.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable source, Object value, boolean selected, boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(
                        source, value, selected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(CampusPlacementSystem.F_SMALL);
                setForeground(selected
                        ? CampusPlacementSystem.C_DARK_TXT : CampusPlacementSystem.C_MUTED);
                setBorder(new EmptyBorder(0, 4, 0, 4));
                if (!selected) {
                    setBackground(row % 2 == 0
                            ? CampusPlacementSystem.C_SURFACE : CampusPlacementSystem.C_ROW_ALT);
                }
                return component;
            }
        });
    }

    static JTextField mkLightField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(CampusPlacementSystem.F_BODY);
        field.setPreferredSize(new Dimension(300, 46));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        field.setBackground(new Color(249, 250, 251));
        field.setForeground(CampusPlacementSystem.C_DARK_TXT);
        field.setCaretColor(CampusPlacementSystem.C_INDIGO);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CampusPlacementSystem.C_BORDER, 1, true),
                new EmptyBorder(0, 12, 0, 12)));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        return field;
    }

    static JTextField mkDkField() {
        JTextField field = new JTextField();
        field.setFont(CampusPlacementSystem.F_BODY);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setBackground(CampusPlacementSystem.C_DARK_FLD);
        field.setForeground(CampusPlacementSystem.C_WHITE);
        field.setCaretColor(CampusPlacementSystem.C_INDIGO);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CampusPlacementSystem.C_DARK_BDR, 1, true),
                new EmptyBorder(0, 10, 0, 10)));
        return field;
    }

    static JLabel mkLightLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(CampusPlacementSystem.F_LABEL);
        label.setForeground(new Color(71, 85, 105));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    static JLabel mkDkLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(CampusPlacementSystem.F_LABEL);
        label.setForeground(new Color(148, 163, 184));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    static void styleCombo(JComboBox<String> combo) {
        combo.setFont(CampusPlacementSystem.F_BODY);
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        combo.setAlignmentX(Component.LEFT_ALIGNMENT);
        combo.setBackground(CampusPlacementSystem.C_DARK_FLD);
        combo.setForeground(CampusPlacementSystem.C_WHITE);
        combo.setBorder(BorderFactory.createLineBorder(CampusPlacementSystem.C_DARK_BDR, 1));
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean selected, boolean hasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, selected, hasFocus);
                label.setBackground(selected
                        ? CampusPlacementSystem.C_INDIGO : new Color(22, 27, 34));
                label.setForeground(CampusPlacementSystem.C_WHITE);
                label.setBorder(new EmptyBorder(5, 10, 5, 10));
                return label;
            }
        });
    }

    static JButton mkBtn(String text, Color background, Color hoverBackground) {
        JButton button = new JButton(text) {
            private boolean hover;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                    @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hover ? hoverBackground : background);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setFont(CampusPlacementSystem.F_BTN);
        button.setForeground(Color.WHITE);
        button.setBackground(background);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(7, 14, 7, 14));
        return button;
    }

    static JLabel makeTagBadge(String text, Color foreground, Color background, Color borderColor) {
        JLabel label = new JLabel("  " + text + "  ");
        label.setFont(new Font("Segoe UI", Font.BOLD, 10));
        label.setForeground(foreground);
        label.setBackground(background);
        label.setOpaque(true);
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1, true),
                new EmptyBorder(3, 6, 3, 6)));
        return label;
    }

    static void startClock(JLabel label) {
        Timer timer = new Timer(1000, e -> {
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            label.setText(String.format("%02d:%02d:%02d  |  %s, %s",
                    now.getHour(), now.getMinute(), now.getSecond(),
                    now.getDayOfWeek().getDisplayName(
                            java.time.format.TextStyle.SHORT, java.util.Locale.ENGLISH),
                    now.toLocalDate()));
        });
        timer.setInitialDelay(0);
        timer.start();
    }

    static Image buildIcon() {
        java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(
                64, 64, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(CampusPlacementSystem.C_INDIGO);
        g2.fillRoundRect(0, 0, 64, 64, 16, 16);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        g2.drawString("🎓", 10, 46);
        g2.dispose();
        return image;
    }

    static void showErr(Component parent, String title, String message) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }

    static void showOk(Component parent, String title, String message) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
}
