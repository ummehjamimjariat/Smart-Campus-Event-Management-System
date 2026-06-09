package com.smartcampus.ui;

import com.smartcampus.model.Person;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

public abstract class BaseDashboard extends JFrame {

    protected final Person currentUser;
    protected JPanel contentArea;
    private   CardLayout cardLayout;

    public BaseDashboard(Person user) {
        this.currentUser = user;
        setTitle("Smart Campus EMS  —  " + user.getDashboardTitle());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        buildFrame();
        loadDefaultPanel();
        setVisible(true);
    }

    private void buildFrame() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIHelper.BG_PAGE);
        root.add(buildTopBar(),  BorderLayout.NORTH);
        root.add(buildSidebar(), BorderLayout.WEST);

        cardLayout  = new CardLayout();
        contentArea = new JPanel(cardLayout);
        contentArea.setBackground(UIHelper.BG_PAGE);
        contentArea.setBorder(new EmptyBorder(24, 24, 24, 24));
        root.add(contentArea, BorderLayout.CENTER);
        setContentPane(root);
    }

    // ── Top Bar ────────────────────────────────────────────────────────────
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout()) {
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(UIHelper.BG_TOPBAR);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(UIHelper.BORDER_COLOR);
                g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
            }
        };
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(0, 60));
        bar.setBorder(new EmptyBorder(0, 24, 0, 24));

        // Left: app name with blue accent
        JPanel leftSide = new JPanel(new BorderLayout());
        leftSide.setOpaque(false);
        JLabel appName = new JLabel("Smart Campus EMS");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        appName.setForeground(UIHelper.ACCENT_BLUE);
        JLabel subName = new JLabel("  Event Management System");
        subName.setFont(UIHelper.FONT_SMALL);
        subName.setForeground(UIHelper.TEXT_SECONDARY);
        JPanel nameRow = new JPanel();
        nameRow.setOpaque(false);
        nameRow.add(appName);
        nameRow.add(subName);
        leftSide.add(nameRow, BorderLayout.WEST);
        bar.add(leftSide, BorderLayout.WEST);

        // Right: user info + logout
        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setBorder(new EmptyBorder(10, 0, 10, 0));

        JLabel userLbl = new JLabel(currentUser.getName());
        userLbl.setFont(UIHelper.FONT_BOLD);
        userLbl.setForeground(UIHelper.TEXT_PRIMARY);

        JLabel roleBadge = UIHelper.badge(currentUser.getRole(), UIHelper.typeColor(currentUser.getRole()));

        JButton logoutBtn = UIHelper.primaryButton("Logout", UIHelper.ACCENT_RED);
        logoutBtn.setPreferredSize(new Dimension(90, 34));
        logoutBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int ok = JOptionPane.showConfirmDialog(BaseDashboard.this,
                        "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
                if (ok == JOptionPane.YES_OPTION) { dispose(); new LoginFrame(); }
            }
        });

        right.add(userLbl);
        right.add(roleBadge);
        right.add(logoutBtn);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    // ── Sidebar ────────────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel() {
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UIHelper.BG_SIDEBAR);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(20, 0, 20, 0));

        // Role badge at top of sidebar
        JPanel roleArea = new JPanel();
        roleArea.setOpaque(false);
        roleArea.setBorder(new EmptyBorder(0, 0, 16, 0));
        JLabel roleLabel = new JLabel(currentUser.getRole());
        roleLabel.setFont(UIHelper.FONT_SMALL);
        roleLabel.setForeground(new Color(150, 190, 255));
        roleArea.add(roleLabel);
        sidebar.add(roleArea);

        // Divider
        JPanel div = new JPanel();
        div.setOpaque(false);
        div.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        div.setBackground(new Color(255,255,255,40));
        div.setBorder(new MatteBorder(0,0,1,0, new Color(255,255,255,40)));
        sidebar.add(div);
        sidebar.add(javax.swing.Box.createVerticalStrut(12));

        for (JButton btn : buildSidebarButtons()) {
            sidebar.add(btn);
        }
        return sidebar;
    }

    protected abstract JButton[] buildSidebarButtons();
    public abstract void loadDefaultPanel();

    protected void showPanel(JPanel panel) {
        contentArea.removeAll();
        contentArea.add(panel);
        contentArea.revalidate();
        contentArea.repaint();
    }

    protected JButton sidebarBtn(String icon, String label) {
        return UIHelper.sidebarButton(icon, label);
    }

    // ── Profile Panel ──────────────────────────────────────────────────────
    protected JPanel buildProfilePanel() {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setBackground(UIHelper.BG_PAGE);
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        p.add(UIHelper.sectionTitle("My Profile"), BorderLayout.NORTH);

        JPanel card = UIHelper.cardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        addProfileRow(card, "Full Name", currentUser.getName());
        addProfileRow(card, "Email",     currentUser.getEmail());
        addProfileRow(card, "Phone",     currentUser.getPhone());
        addProfileRow(card, "Role",      currentUser.getRole());
        addProfileRow(card, "User ID",   currentUser.getId());

        p.add(card, BorderLayout.CENTER);
        return p;
    }

    protected void addProfileRow(JPanel card, String key, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(8, 0, 8, 0));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        JLabel keyLbl = UIHelper.label(key, UIHelper.FONT_BOLD, UIHelper.TEXT_SECONDARY);
        keyLbl.setPreferredSize(new Dimension(140, 24));
        JLabel valLbl = UIHelper.label(value != null ? value : "N/A", UIHelper.FONT_BODY, UIHelper.TEXT_PRIMARY);

        row.add(keyLbl, BorderLayout.WEST);
        row.add(valLbl, BorderLayout.CENTER);

        // Bottom separator
        row.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0,0,1,0, UIHelper.BORDER_COLOR),
            new EmptyBorder(8, 0, 8, 0)
        ));
        card.add(row);
    }
}
