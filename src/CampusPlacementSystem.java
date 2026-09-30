import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * ============================================================
 *  Real-Time Campus Placement Management System
 *  Author  : Karthikeyan C K | Roll No: 25IT347
 *  College : St. Joseph's College of Engineering
 *  Stack   : Core Java + Swing + AWT + JDBC (MySQL)
 *  Version : 2.0 — Enterprise Edition
 * ============================================================
 */
public class CampusPlacementSystem extends JFrame {

    // ─── Database ─────────────────────────────────────────────
    private static final String DB_URL = setting("CPS_DB_URL",
            "jdbc:mysql://localhost:3306/campus_placement_db" +
                    "?useSSL=false&serverTimezone=Asia/Kolkata&allowPublicKeyRetrieval=true");
    private static final String DB_USER = setting("CPS_DB_USER", "root");
    private static final String DB_PASS = setting("CPS_DB_PASS", "");

    private static String setting(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null || value.trim().isEmpty() ? defaultValue : value.trim();
    }

    // ─── Colour Palette — Premium Dark/Light Hybrid ───────────
    static final Color C_DARK     = new Color(13,  17,  23);
    static final Color C_SIDEBAR  = new Color(22,  27,  34);
    static final Color C_SURFACE  = new Color(255, 255, 255);
    static final Color C_CARD     = new Color(246, 248, 250);
    static final Color C_BORDER   = new Color(208, 215, 222);
    static final Color C_INDIGO   = new Color(99,  102, 241);
    static final Color C_INDIGO2  = new Color(79,  70,  229);
    static final Color C_GREEN    = new Color(34,  197, 94);
    static final Color C_GREEN2   = new Color(21,  128, 61);
    static final Color C_RED      = new Color(239, 68,  68);
    static final Color C_RED2     = new Color(185, 28,  28);
    static final Color C_AMBER    = new Color(245, 158, 11);
    static final Color C_PURPLE   = new Color(168, 85,  247);
    static final Color C_TEAL     = new Color(6,   182, 212);
    static final Color C_TEAL2    = new Color(8,   145, 178);
    static final Color C_WHITE    = new Color(248, 250, 252);
    static final Color C_MUTED    = new Color(139, 148, 158);
    static final Color C_DARK_TXT = new Color(31,  35,  40);
    static final Color C_HDR      = new Color(33,  38,  45);
    static final Color C_ROW_SEL  = new Color(225, 239, 255);
    static final Color C_ROW_ALT  = new Color(252, 253, 254);
    static final Color C_DARK_BDR = new Color(48,  54,  61);
    static final Color C_DARK_FLD = new Color(13,  17,  23);

    // ─── Fonts ────────────────────────────────────────────────
    static final Font F_HEAD  = new Font("Segoe UI", Font.BOLD,  15);
    static final Font F_LABEL = new Font("Segoe UI", Font.BOLD,  12);
    static final Font F_BODY  = new Font("Segoe UI", Font.PLAIN, 13);
    static final Font F_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    static final Font F_BTN   = new Font("Segoe UI", Font.BOLD,  12);

    // ─── Layout ───────────────────────────────────────────────
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel     cardPanel  = new JPanel(cardLayout);

    // ─── Login ────────────────────────────────────────────────
    private JTextField     loginUser;
    private JPasswordField loginPass;
    private boolean        passShown = false;

    // ─── Form fields ──────────────────────────────────────────
    private JTextField       fName, fRoll, fDept, fCompany, fPkg;
    private JComboBox<String> cbStatus;
    private JLabel           lblFormHead;

    // ─── Table ────────────────────────────────────────────────
    private JTable            table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    // ─── Toolbar controls ─────────────────────────────────────
    private JTextField        searchField;
    private JComboBox<String> filterDept, filterStatus;

    // ─── KPI labels ───────────────────────────────────────────
    private JLabel kpiTotal, kpiPlaced, kpiNot, kpiAvg, kpiTop;

    // ─── Status bar ───────────────────────────────────────────
    private JLabel lblStatus;

    // ─── State ────────────────────────────────────────────────
    private int selectedId = -1;

    // ═══════════════════════════════════════════════════════════
    //  ENTRY POINT
    // ═══════════════════════════════════════════════════════════
    public static void main(String[] args) {
        try { Class.forName("com.mysql.cj.jdbc.Driver"); }
        catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null,
                "MySQL JDBC Driver not found!\nPlace mysql-connector-j.jar in the lib\\ folder.",
                "Driver Missing", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        SwingUtilities.invokeLater(CampusPlacementSystem::showSplash);
    }

    // ═══════════════════════════════════════════════════════════
    //  SPLASH SCREEN
    // ═══════════════════════════════════════════════════════════
    private static void showSplash() {
        PlacementSplash.show();
    }

    // ═══════════════════════════════════════════════════════════
    //  CONSTRUCTOR
    // ═══════════════════════════════════════════════════════════
    public CampusPlacementSystem() {
        super("Campus Placement Management System — St. Joseph's College of Engineering");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1366, 860);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);
        setIconImage(PlacementUi.buildIcon());

        cardPanel.add(buildLogin(),     "LOGIN");
        cardPanel.add(buildDashboard(), "DASHBOARD");
        add(cardPanel);
        cardLayout.show(cardPanel, "LOGIN");
    }

    // ═══════════════════════════════════════════════════════════
    //  SCREEN 1 — LOGIN
    // ═══════════════════════════════════════════════════════════
    private JPanel buildLogin() {
        JPanel root = new JPanel(new GridLayout(1, 2));

        // ── LEFT — Branding ──────────────────────────────────
        JPanel left = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, C_DARK, getWidth(), getHeight(), new Color(20, 20, 50)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(99, 102, 241, 25));
                g2.fillOval(-60, getHeight() - 200, 300, 300);
                g2.setColor(new Color(99, 102, 241, 15));
                g2.fillOval(getWidth() - 100, -80, 250, 250);
            }
        };
        left.setOpaque(false);

        JPanel brand = new JPanel();
        brand.setOpaque(false);
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));

        JLabel ico = new JLabel("🎓", SwingConstants.CENTER);
        ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 72));
        ico.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel t1 = new JLabel("Campus Placement", SwingConstants.CENTER);
        t1.setFont(new Font("Segoe UI", Font.BOLD, 28));
        t1.setForeground(C_WHITE);
        t1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel t2 = new JLabel("Management System", SwingConstants.CENTER);
        t2.setFont(new Font("Segoe UI", Font.PLAIN, 19));
        t2.setForeground(C_INDIGO);
        t2.setAlignmentX(Component.CENTER_ALIGNMENT);

        brand.add(ico);
        brand.add(Box.createVerticalStrut(14));
        brand.add(t1);
        brand.add(Box.createVerticalStrut(4));
        brand.add(t2);
        brand.add(Box.createVerticalStrut(28));

        // Feature bullets
        String[][] feats = {
            {"📋", "Manage student placement records"},
            {"🔍", "Search, filter and sort data live"},
            {"📊", "Live KPI statistics dashboard"},
            {"📥", "Export records to CSV instantly"},
        };
        for (String[] f : feats) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
            row.setOpaque(false);
            JLabel ic = new JLabel(f[0]);
            ic.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
            JLabel tx = new JLabel(f[1]);
            tx.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            tx.setForeground(new Color(100, 116, 139));
            row.add(ic); row.add(tx);
            brand.add(row);
            brand.add(Box.createVerticalStrut(6));
        }

        brand.add(Box.createVerticalStrut(28));
        JSeparator sp = new JSeparator();
        sp.setMaximumSize(new Dimension(310, 1));
        sp.setForeground(new Color(40, 47, 58));
        brand.add(sp);
        brand.add(Box.createVerticalStrut(14));

        JLabel cl = new JLabel("St. Joseph's College of Engineering", SwingConstants.CENTER);
        cl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cl.setForeground(new Color(100, 116, 139));
        cl.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel dl = new JLabel("Karthikeyan C K  ·  25IT347", SwingConstants.CENTER);
        dl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        dl.setForeground(new Color(71, 85, 105));
        dl.setAlignmentX(Component.CENTER_ALIGNMENT);
        brand.add(cl);
        brand.add(Box.createVerticalStrut(4));
        brand.add(dl);

        left.add(brand);

        // ── RIGHT — Form ─────────────────────────────────────
        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(C_SURFACE);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(10, 64, 10, 64));

        // Admin badge
        JLabel badge = PlacementUi.makeTagBadge("ADMIN PORTAL", C_INDIGO,
                new Color(238, 242, 255), new Color(199, 210, 254));
        badge.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel welcome = new JLabel("Welcome Back 👋");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 26));
        welcome.setForeground(C_DARK_TXT);
        welcome.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel wsub = new JLabel("Sign in to access the administrator dashboard");
        wsub.setFont(F_BODY);
        wsub.setForeground(C_MUTED);
        wsub.setAlignmentX(Component.LEFT_ALIGNMENT);

        form.add(badge);
        form.add(Box.createVerticalStrut(18));
        form.add(welcome);
        form.add(Box.createVerticalStrut(4));
        form.add(wsub);
        form.add(Box.createVerticalStrut(32));

        // Username
        form.add(PlacementUi.mkLightLabel("Username"));
        form.add(Box.createVerticalStrut(6));
        loginUser = PlacementUi.mkLightField("admin");
        form.add(loginUser);
        form.add(Box.createVerticalStrut(16));

        // Password + eye toggle
        form.add(PlacementUi.mkLightLabel("Password"));
        form.add(Box.createVerticalStrut(6));
        JPanel passRow = new JPanel(new BorderLayout());
        passRow.setOpaque(false);
        passRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        passRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginPass = new JPasswordField();
        loginPass.setFont(F_BODY);
        loginPass.setBackground(new Color(249, 250, 251));
        loginPass.setForeground(C_DARK_TXT);
        loginPass.setCaretColor(C_INDIGO);
        loginPass.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDER, 1, true),
                new EmptyBorder(0, 12, 0, 6)));
        JButton eyeBtn = new JButton("👁");
        eyeBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));
        eyeBtn.setBackground(new Color(249, 250, 251));
        eyeBtn.setFocusPainted(false);
        eyeBtn.setBorderPainted(false);
        eyeBtn.setContentAreaFilled(true);
        eyeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        eyeBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 1, 1, C_BORDER),
                new EmptyBorder(0, 8, 0, 8)));
        eyeBtn.addActionListener(e -> {
            passShown = !passShown;
            loginPass.setEchoChar(passShown ? '\0' : '•');
            eyeBtn.setText(passShown ? "🙈" : "👁");
        });
        passRow.add(loginPass, BorderLayout.CENTER);
        passRow.add(eyeBtn,    BorderLayout.EAST);
        form.add(passRow);
        form.add(Box.createVerticalStrut(28));

        // Sign In button
        JButton btnLogin = PlacementUi.mkBtn("  Sign In  →", C_INDIGO, C_INDIGO2);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(btnLogin);
        form.add(Box.createVerticalStrut(18));

        // Hint box
        JPanel hint = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        hint.setOpaque(false);
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel hIco = new JLabel("ℹ");
        hIco.setFont(F_SMALL);
        hIco.setForeground(C_INDIGO);
        JLabel hTxt = new JLabel("Default: admin / admin123");
        hTxt.setFont(F_SMALL);
        hTxt.setForeground(C_MUTED);
        hint.add(hIco);
        hint.add(hTxt);
        form.add(hint);

        right.add(form);

        // Actions
        ActionListener doLogin = e -> performLogin();
        btnLogin.addActionListener(doLogin);
        loginPass.addActionListener(doLogin);
        loginUser.addActionListener(e -> loginPass.requestFocus());

        root.add(left);
        root.add(right);
        return root;
    }

    // ═══════════════════════════════════════════════════════════
    //  SCREEN 2 — DASHBOARD
    // ═══════════════════════════════════════════════════════════
    private JPanel buildDashboard() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(C_CARD);

        root.add(buildHeader(), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(C_CARD);
        body.add(buildKpiRow(),   BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                buildFormPanel(), buildTablePanel());
        split.setDividerLocation(388);
        split.setDividerSize(3);
        split.setBorder(null);
        split.setResizeWeight(0.0);
        body.add(split, BorderLayout.CENTER);
        root.add(body, BorderLayout.CENTER);
        return root;
    }

    // ── Header ───────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(C_DARK);
        h.setBorder(new EmptyBorder(12, 24, 12, 24));
        h.setPreferredSize(new Dimension(0, 66));

        JPanel lft = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        lft.setOpaque(false);
        JLabel ic = new JLabel("🎓");
        ic.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
        JPanel tb = new JPanel();
        tb.setOpaque(false);
        tb.setLayout(new BoxLayout(tb, BoxLayout.Y_AXIS));
        JLabel tt = new JLabel("Campus Placement Management System");
        tt.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tt.setForeground(C_WHITE);
        JLabel ts = new JLabel("Karthikeyan C K  —  25IT347  —  St. Joseph's College of Engineering");
        ts.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        ts.setForeground(C_MUTED);
        tb.add(tt); tb.add(ts);
        lft.add(ic); lft.add(tb);

        JPanel rgt = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rgt.setOpaque(false);
        JLabel clock = new JLabel();
        clock.setFont(F_SMALL);
        clock.setForeground(C_MUTED);
        PlacementUi.startClock(clock);
        JButton logout = PlacementUi.mkBtn("⏻  Logout", C_RED, C_RED2);
        logout.setPreferredSize(new Dimension(105, 34));
        logout.addActionListener(e -> performLogout());
        rgt.add(clock);
        rgt.add(logout);

        h.add(lft, BorderLayout.WEST);
        h.add(rgt, BorderLayout.EAST);
        return h;
    }

    // ── KPI Row ───────────────────────────────────────────────
    private JPanel buildKpiRow() {
        JPanel row = new JPanel(new GridLayout(1, 5, 12, 0));
        row.setBackground(C_CARD);
        row.setBorder(new EmptyBorder(14, 14, 10, 14));

        kpiTotal  = new JLabel("—");
        kpiPlaced = new JLabel("—");
        kpiNot    = new JLabel("—");
        kpiAvg    = new JLabel("—");
        kpiTop    = new JLabel("—");

        row.add(mkKpiCard("Total Students", kpiTotal,  "👥", C_INDIGO,  new Color(238, 242, 255)));
        row.add(mkKpiCard("Placed",         kpiPlaced, "✅", C_GREEN,   new Color(240, 253, 244)));
        row.add(mkKpiCard("Not Placed",     kpiNot,    "❌", C_RED,     new Color(254, 242, 242)));
        row.add(mkKpiCard("Avg Placed Package", kpiAvg, "💰", C_AMBER,  new Color(255, 251, 235)));
        row.add(mkKpiCard("Top Placed Package", kpiTop, "🏆", C_PURPLE, new Color(250, 245, 255)));
        return row;
    }

    private JPanel mkKpiCard(String title, JLabel valLbl, String icon, Color accent, Color iconBg) {
        JPanel card = new JPanel(new BorderLayout(12, 0));
        card.setBackground(C_SURFACE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(14, 14, 14, 14)));

        // Icon circle
        JLabel icoLbl = new JLabel(icon, SwingConstants.CENTER);
        icoLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        JPanel icoBox = new JPanel(new GridBagLayout());
        icoBox.setBackground(iconBg);
        icoBox.setPreferredSize(new Dimension(50, 50));
        icoBox.setBorder(BorderFactory.createLineBorder(accent, 1, true));
        icoBox.add(icoLbl);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        valLbl.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valLbl.setForeground(accent);
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(F_SMALL);
        titleLbl.setForeground(C_MUTED);
        info.add(valLbl);
        info.add(titleLbl);

        card.add(icoBox, BorderLayout.WEST);
        card.add(info,   BorderLayout.CENTER);
        return card;
    }

    // ── Sidebar Form ──────────────────────────────────────────
    private JPanel buildFormPanel() {
        JPanel sb = new JPanel();
        sb.setBackground(C_SIDEBAR);
        sb.setLayout(new BoxLayout(sb, BoxLayout.Y_AXIS));
        sb.setBorder(new EmptyBorder(20, 18, 18, 18));
        sb.setPreferredSize(new Dimension(388, 0));

        lblFormHead = new JLabel("➕  Add New Record");
        lblFormHead.setFont(F_HEAD);
        lblFormHead.setForeground(C_WHITE);
        lblFormHead.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(new Color(40, 46, 56));
        sb.add(lblFormHead);
        sb.add(Box.createVerticalStrut(8));
        sb.add(sep);
        sb.add(Box.createVerticalStrut(16));

        // Fields
        sb.add(PlacementUi.mkDkLabel("Student Name")); sb.add(Box.createVerticalStrut(4));
        fName = PlacementUi.mkDkField(); sb.add(fName); sb.add(Box.createVerticalStrut(11));

        sb.add(PlacementUi.mkDkLabel("Roll Number")); sb.add(Box.createVerticalStrut(4));
        fRoll = PlacementUi.mkDkField(); sb.add(fRoll); sb.add(Box.createVerticalStrut(11));

        sb.add(PlacementUi.mkDkLabel("Department")); sb.add(Box.createVerticalStrut(4));
        fDept = PlacementUi.mkDkField(); sb.add(fDept); sb.add(Box.createVerticalStrut(11));

        sb.add(PlacementUi.mkDkLabel("Company")); sb.add(Box.createVerticalStrut(4));
        fCompany = PlacementUi.mkDkField(); sb.add(fCompany); sb.add(Box.createVerticalStrut(11));

        sb.add(PlacementUi.mkDkLabel("Placement Status")); sb.add(Box.createVerticalStrut(4));
        cbStatus = new JComboBox<>(new String[]{"Placed", "Not Placed", "In Process", "On Hold"});
        PlacementUi.styleCombo(cbStatus);
        sb.add(cbStatus);
        sb.add(Box.createVerticalStrut(11));

        sb.add(PlacementUi.mkDkLabel("Package (LPA)")); sb.add(Box.createVerticalStrut(4));
        fPkg = PlacementUi.mkDkField(); sb.add(fPkg);
        sb.add(Box.createVerticalStrut(20));

        // 2x2 button grid
        JPanel btnGrid = new JPanel(new GridLayout(2, 2, 8, 8));
        btnGrid.setOpaque(false);
        btnGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 96));

        JButton btnAdd    = PlacementUi.mkBtn("➕  Add",    C_GREEN,  C_GREEN2);
        JButton btnUpdate = PlacementUi.mkBtn("✏  Update", C_INDIGO, C_INDIGO2);
        JButton btnDelete = PlacementUi.mkBtn("🗑  Delete", C_RED,    C_RED2);
        JButton btnClear  = PlacementUi.mkBtn("✖  Clear",  new Color(71, 85, 105), new Color(51, 65, 85));

        btnGrid.add(btnAdd);
        btnGrid.add(btnUpdate);
        btnGrid.add(btnDelete);
        btnGrid.add(btnClear);
        sb.add(btnGrid);

        // Export button
        sb.add(Box.createVerticalStrut(10));
        JButton btnExport = PlacementUi.mkBtn("📥  Export to CSV", C_TEAL, C_TEAL2);
        btnExport.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnExport.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        sb.add(btnExport);

        // Actions
        btnAdd.addActionListener(e    -> doAdd());
        btnUpdate.addActionListener(e -> doUpdate());
        btnDelete.addActionListener(e -> doDelete());
        btnClear.addActionListener(e  -> clearForm());
        btnExport.addActionListener(e -> exportCSV());

        return sb;
    }

    // ── Table Panel ───────────────────────────────────────────
    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(C_SURFACE);

        // ─ Toolbar
        JPanel toolbar = new JPanel(new BorderLayout(10, 0));
        toolbar.setBackground(C_SURFACE);
        toolbar.setBorder(new EmptyBorder(14, 16, 10, 16));

        JLabel tblTitle = new JLabel("📋  Placement Records");
        tblTitle.setFont(F_HEAD);
        tblTitle.setForeground(C_DARK_TXT);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        controls.setOpaque(false);

        // Search box
        JPanel searchBox = new JPanel(new BorderLayout());
        searchBox.setBackground(C_SURFACE);
        searchBox.setBorder(BorderFactory.createLineBorder(C_BORDER, 1, true));
        JLabel searchIco = new JLabel(" 🔍 ");
        searchIco.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
        searchIco.setBackground(C_SURFACE);
        searchIco.setOpaque(true);
        searchField = new JTextField(13);
        searchField.setFont(F_BODY);
        searchField.setBorder(new EmptyBorder(4, 2, 4, 8));
        searchField.setToolTipText("Search by name, roll, company...");
        searchBox.add(searchIco,   BorderLayout.WEST);
        searchBox.add(searchField, BorderLayout.CENTER);

        // Dept filter
        filterDept = new JComboBox<>(new String[]{
            "All Departments", "Computer Science", "Information Tech",
            "Electronics", "Mechanical", "Civil Engineering", "IT"});
        filterDept.setFont(F_SMALL);
        filterDept.setPreferredSize(new Dimension(148, 30));

        // Status filter
        filterStatus = new JComboBox<>(new String[]{
            "All Status", "Placed", "Not Placed", "In Process", "On Hold"});
        filterStatus.setFont(F_SMALL);
        filterStatus.setPreferredSize(new Dimension(118, 30));

        JButton btnRefresh = PlacementUi.mkBtn("⟳", C_TEAL, C_TEAL2);
        btnRefresh.setPreferredSize(new Dimension(36, 30));
        btnRefresh.addActionListener(e -> { loadTableData(); applyFilter(); });

        controls.add(searchBox);
        controls.add(filterDept);
        controls.add(filterStatus);
        controls.add(btnRefresh);

        toolbar.add(tblTitle,  BorderLayout.WEST);
        toolbar.add(controls,  BorderLayout.EAST);
        panel.add(toolbar, BorderLayout.NORTH);

        // ─ Table
        String[] cols = {"ID", "Student Name", "Roll No", "Department", "Company", "Status", "Package (LPA)"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) {
                if (c == 0) return Integer.class;
                if (c == 6) return Double.class;
                return String.class;
            }
        };
        table = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        PlacementUi.styleTable(table);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) fillForm();
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, C_BORDER));
        scroll.getViewport().setBackground(C_SURFACE);
        panel.add(scroll, BorderLayout.CENTER);

        // ─ Status bar
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(246, 248, 250));
        statusBar.setBorder(new MatteBorder(1, 0, 0, 0, C_BORDER));
        statusBar.setPreferredSize(new Dimension(0, 30));
        lblStatus = new JLabel("  Ready");
        lblStatus.setFont(F_SMALL);
        lblStatus.setForeground(C_MUTED);
        JLabel copy = new JLabel(
            "Campus Placement System  ·  Karthikeyan C K  ·  25IT347  ·  St. Joseph's College of Engineering   ");
        copy.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        copy.setForeground(new Color(190, 200, 210));
        statusBar.add(lblStatus, BorderLayout.WEST);
        statusBar.add(copy,      BorderLayout.EAST);
        panel.add(statusBar, BorderLayout.SOUTH);

        // ─ Filter listeners
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { applyFilter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { applyFilter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
        });
        filterDept.addActionListener(e -> applyFilter());
        filterStatus.addActionListener(e -> applyFilter());

        loadTableData();
        return panel;
    }

    // ═══════════════════════════════════════════════════════════
    //  LIVE FILTER
    // ═══════════════════════════════════════════════════════════
    private void applyFilter() {
        String txt    = searchField.getText().trim();
        String dept   = (String) filterDept.getSelectedItem();
        String status = (String) filterStatus.getSelectedItem();

        java.util.List<RowFilter<DefaultTableModel, Object>> filters = new ArrayList<>();

        if (!txt.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + txt));
        }
        if (!"All Departments".equals(dept)) {
            filters.add(RowFilter.regexFilter("(?i)^" + dept + "$", 3));
        }
        if (!"All Status".equals(status)) {
            filters.add(RowFilter.regexFilter("(?i)^" + status + "$", 5));
        }

        if (filters.isEmpty()) {
            sorter.setRowFilter(null);
        } else if (filters.size() == 1) {
            sorter.setRowFilter(filters.get(0));
        } else {
            sorter.setRowFilter(RowFilter.andFilter(filters));
        }

        if (lblStatus != null) {
            lblStatus.setText("  " + table.getRowCount() + " record(s) shown");
        }
    }

    // ═══════════════════════════════════════════════════════════
    //  JDBC OPERATIONS
    // ═══════════════════════════════════════════════════════════
    private Connection conn() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    /** Login validation */
    private void performLogin() {
        String u = loginUser.getText().trim();
        String p = new String(loginPass.getPassword()).trim();
        if (u.isEmpty() || p.isEmpty()) {
            showErr("Login", "Please enter both username and password.");
            return;
        }
        String sql = "SELECT id FROM admin_users WHERE username=? AND password=?";
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, u);
            ps.setString(2, p);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    loginUser.setText("");
                    loginPass.setText("");
                    loadTableData();
                    cardLayout.show(cardPanel, "DASHBOARD");
                } else {
                    showErr("Login Failed", "Invalid username or password.\nPlease try again.");
                }
            }
        } catch (SQLException ex) {
            showErr("DB Error", "Cannot connect to database.\n\n" + ex.getMessage());
        }
    }

    /** Load/refresh all table data */
    private void loadTableData() {
        tableModel.setRowCount(0);
        String sql = "SELECT id,student_name,roll_no,department,company,status,package_lpa " +
                     "FROM placement_records ORDER BY id";
        int total = 0, placed = 0, notPlaced = 0;
        double placedPkg = 0, topPlacedPkg = 0;
        try (Connection c = conn();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int    id   = rs.getInt("id");
                String name = rs.getString("student_name");
                String roll = rs.getString("roll_no");
                String dept = rs.getString("department");
                String co   = rs.getString("company");
                String st   = rs.getString("status");
                double pkg  = rs.getDouble("package_lpa");
                tableModel.addRow(new Object[]{id, name, roll, dept, co, st, pkg});
                total++;
                if ("Placed".equalsIgnoreCase(st)) {
                    placed++;
                    placedPkg += pkg;
                    if (pkg > topPlacedPkg) topPlacedPkg = pkg;
                } else if ("Not Placed".equalsIgnoreCase(st)) {
                    notPlaced++;
                }
            }
            final int    T  = total, PL = placed, NP = notPlaced;
            final String AV = placed > 0 ? String.format("%.1f", placedPkg / placed) + " LPA" : "—";
            final String TP = topPlacedPkg > 0 ? String.format("%.1f", topPlacedPkg) + " LPA" : "—";
            SwingUtilities.invokeLater(() -> {
                kpiTotal.setText(String.valueOf(T));
                kpiPlaced.setText(String.valueOf(PL));
                kpiNot.setText(String.valueOf(NP));
                kpiAvg.setText(AV);
                kpiTop.setText(TP);
                if (lblStatus != null) lblStatus.setText("  " + T + " total record(s)");
            });
        } catch (SQLException ex) {
            showErr("Load Error", "Failed to load records.\n\n" + ex.getMessage());
        }
    }

    /** INSERT */
    private void doAdd() {
        if (!validateForm()) return;
        String sql = "INSERT INTO placement_records" +
                     "(student_name,roll_no,department,company,status,package_lpa) VALUES(?,?,?,?,?,?)";
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, fName.getText().trim());
            ps.setString(2, fRoll.getText().trim());
            ps.setString(3, fDept.getText().trim());
            ps.setString(4, fCompany.getText().trim());
            ps.setString(5, (String) cbStatus.getSelectedItem());
            ps.setDouble(6, Double.parseDouble(fPkg.getText().trim()));
            if (ps.executeUpdate() > 0) {
                showOk("Record Added", "✅ Placement record added successfully!");
                clearForm();
                loadTableData();
                applyFilter();
                lblStatus.setText("  Record added successfully");
            }
        } catch (SQLIntegrityConstraintViolationException ex) {
            showErr("Duplicate Roll No", "A record with this Roll Number already exists.");
        } catch (SQLException ex) {
            showErr("DB Error", "Failed to add record.\n\n" + ex.getMessage());
        }
    }

    /** UPDATE */
    private void doUpdate() {
        if (selectedId == -1) {
            showErr("No Selection", "Please click a row in the table to select a record first.");
            return;
        }
        if (!validateForm()) return;
        String sql = "UPDATE placement_records " +
                     "SET student_name=?,roll_no=?,department=?,company=?,status=?,package_lpa=? WHERE id=?";
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, fName.getText().trim());
            ps.setString(2, fRoll.getText().trim());
            ps.setString(3, fDept.getText().trim());
            ps.setString(4, fCompany.getText().trim());
            ps.setString(5, (String) cbStatus.getSelectedItem());
            ps.setDouble(6, Double.parseDouble(fPkg.getText().trim()));
            ps.setInt(7, selectedId);
            if (ps.executeUpdate() > 0) {
                showOk("Record Updated", "✏  Record updated successfully!");
                clearForm();
                loadTableData();
                applyFilter();
                lblStatus.setText("  Record updated");
            }
        } catch (SQLException ex) {
            showErr("DB Error", "Failed to update record.\n\n" + ex.getMessage());
        }
    }

    /** DELETE */
    private void doDelete() {
        if (selectedId == -1) {
            showErr("No Selection", "Please click a row in the table to select a record first.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete this record permanently?\nThis action cannot be undone.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        String sql = "DELETE FROM placement_records WHERE id=?";
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, selectedId);
            if (ps.executeUpdate() > 0) {
                showOk("Deleted", "🗑  Record deleted successfully!");
                clearForm();
                loadTableData();
                applyFilter();
                lblStatus.setText("  Record deleted");
            }
        } catch (SQLException ex) {
            showErr("DB Error", "Failed to delete record.\n\n" + ex.getMessage());
        }
    }

    /** Logout */
    private void performLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Logout", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            clearForm();
            tableModel.setRowCount(0);
            cardLayout.show(cardPanel, "LOGIN");
        }
    }

    /** Export current table view to CSV */
    private void exportCSV() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("placement_records.csv"));
        fc.setDialogTitle("Save CSV File");
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File exportFile = fc.getSelectedFile();
        if (!exportFile.getName().toLowerCase(Locale.ROOT).endsWith(".csv")) {
            exportFile = new File(exportFile.getPath() + ".csv");
        }
        try (PrintWriter pw = new PrintWriter(exportFile, "UTF-8")) {
            // Header
            pw.println(csvEscape("ID") + "," + csvEscape("Student Name") + ","
                    + csvEscape("Roll No") + "," + csvEscape("Department") + ","
                    + csvEscape("Company") + "," + csvEscape("Status") + ","
                    + csvEscape("Package (LPA)"));
            // Data rows (respects current filter)
            for (int r = 0; r < table.getRowCount(); r++) {
                StringBuilder sb = new StringBuilder();
                for (int col = 0; col < tableModel.getColumnCount(); col++) {
                    if (col > 0) sb.append(",");
                    int modelRow = table.convertRowIndexToModel(r);
                    Object val = tableModel.getValueAt(modelRow, col);
                    sb.append(csvEscape(val == null ? "" : val.toString()));
                }
                pw.println(sb);
            }
            showOk("Export Successful",
                "✅ Data exported successfully!\n\nFile saved at:\n" + exportFile.getAbsolutePath());
            lblStatus.setText("  Exported to " + exportFile.getName());
        } catch (IOException ex) {
            showErr("Export Failed", "Could not write file.\n\n" + ex.getMessage());
        }
    }

    private String csvEscape(String value) {
        String normalized = value.replace("\r", " ").replace("\n", " ");
        String formulaCheck = normalized.stripLeading();
        if (!formulaCheck.isEmpty() && "=+-@\t".indexOf(formulaCheck.charAt(0)) >= 0) {
            normalized = "'" + normalized;
        }
        return "\"" + normalized.replace("\"", "\"\"") + "\"";
    }

    // ═══════════════════════════════════════════════════════════
    //  FORM HELPERS
    // ═══════════════════════════════════════════════════════════
    private void fillForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int mr = table.convertRowIndexToModel(row);
        selectedId = (int)   tableModel.getValueAt(mr, 0);
        fName.setText((String)    tableModel.getValueAt(mr, 1));
        fRoll.setText((String)    tableModel.getValueAt(mr, 2));
        fDept.setText((String)    tableModel.getValueAt(mr, 3));
        fCompany.setText((String) tableModel.getValueAt(mr, 4));
        cbStatus.setSelectedItem( tableModel.getValueAt(mr, 5));
        fPkg.setText(String.valueOf(tableModel.getValueAt(mr, 6)));
        lblFormHead.setText("✏  Edit Record  (ID: " + selectedId + ")");
        resetAllBorders();
    }

    private void clearForm() {
        selectedId = -1;
        fName.setText(""); fRoll.setText(""); fDept.setText("");
        fCompany.setText(""); cbStatus.setSelectedIndex(0); fPkg.setText("");
        lblFormHead.setText("➕  Add New Record");
        table.clearSelection();
        resetAllBorders();
        fName.requestFocus();
    }

    private boolean validateForm() {
        boolean ok = true;
        ok = checkField(fName,    ok);
        ok = checkField(fRoll,    ok);
        ok = checkField(fDept,    ok);
        ok = checkField(fCompany, ok);
        if (fPkg.getText().trim().isEmpty()) {
            setErrBorder(fPkg); ok = false;
        } else {
            try {
                double packageLpa = Double.parseDouble(fPkg.getText().trim());
                if (!Double.isFinite(packageLpa) || packageLpa < 0) {
                    setErrBorder(fPkg); ok = false;
                } else {
                    resetBorder(fPkg);
                }
            } catch (NumberFormatException e) {
                setErrBorder(fPkg); ok = false;
            }
        }
        if (!ok) showErr("Validation Error",
            "Please fill in all required fields.\nFields highlighted in red are invalid or empty.");
        return ok;
    }

    private boolean checkField(JTextField f, boolean prev) {
        if (f.getText().trim().isEmpty()) { setErrBorder(f); return false; }
        resetBorder(f); return prev;
    }

    private void setErrBorder(JTextField f) {
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_RED, 2, true),
                new EmptyBorder(0, 10, 0, 10)));
    }

    private void resetBorder(JTextField f) {
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_DARK_BDR, 1, true),
                new EmptyBorder(0, 10, 0, 10)));
    }

    private void resetAllBorders() {
        resetBorder(fName); resetBorder(fRoll);
        resetBorder(fDept); resetBorder(fCompany); resetBorder(fPkg);
    }

    private void showErr(String title, String msg) {
        PlacementUi.showErr(this, title, msg);
    }

    private void showOk(String title, String msg) {
        PlacementUi.showOk(this, title, msg);
    }
}
