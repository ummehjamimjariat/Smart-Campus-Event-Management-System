package com.smartcampus.ui;

import com.smartcampus.exception.AuthenticationException;
import com.smartcampus.exception.SmartCampusException;
import com.smartcampus.model.Person;
import com.smartcampus.service.AuthService;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class LoginFrame extends JFrame {

    private final AuthService authService = new AuthService();

    public LoginFrame() {
        setTitle("Smart Campus Event Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 680);
        setMinimumSize(new java.awt.Dimension(900, 600));
        setLocationRelativeTo(null);
        setResizable(true);
        buildUI();
        setVisible(true);
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIHelper.BG_PAGE);

        // Left panel
        JPanel left = buildLeftPanel();
        left.setPreferredSize(new Dimension(380, 620));
        root.add(left, BorderLayout.WEST);

        // Right panel (tabs)
        JPanel right = new JPanel(new BorderLayout());
        right.setBackground(UIHelper.BG_PAGE);
        right.setBorder(new EmptyBorder(40, 40, 40, 40));

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(UIHelper.BG_CARD);
        tabs.setForeground(UIHelper.TEXT_PRIMARY);
        tabs.setFont(UIHelper.FONT_BODY);
        tabs.addTab("Sign In",  buildSignInPanel());
        tabs.addTab("Register", buildRegisterPanel());
        right.add(tabs, BorderLayout.CENTER);
        root.add(right, BorderLayout.CENTER);

        setContentPane(root);
    }

    // ── Left branding panel ────────────────────────────────────────────────
    private JPanel buildLeftPanel() {
        JPanel p = new JPanel() {
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, new Color(26,54,93), 0, getHeight(), new Color(15,35,70)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                // decorative circles
                g2.setColor(new Color(255,255,255,15));
                g2.fillOval(-60, -60, 300, 300);
                g2.setColor(new Color(100,160,255,20));
                g2.fillOval(80, 300, 250, 250);
            }
        };
        p.setLayout(new GridBagLayout());
        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBorder(new EmptyBorder(0, 40, 0, 40));

        JLabel icon = new JLabel("🎓", JLabel.CENTER);
        icon.setFont(new Font("Segoe UI", Font.PLAIN, 56));
        icon.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        JLabel name = UIHelper.label("Smart Campus", UIHelper.FONT_TITLE, java.awt.Color.WHITE);
        name.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        JLabel sub = UIHelper.label("Event Management System", UIHelper.FONT_BODY, new Color(180, 210, 255));
        sub.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        inner.add(icon);
        inner.add(javax.swing.Box.createVerticalStrut(12));
        inner.add(name);
        inner.add(javax.swing.Box.createVerticalStrut(6));
        inner.add(sub);
        inner.add(javax.swing.Box.createVerticalStrut(40));

        String[] features = {"Discover & Register for Events",
                "Manage Academic, Cultural & Sports",
                "Real-time Seat Availability",
                "Persistent Data with File Storage"};
        for (String f : features) {
            JLabel fl = UIHelper.label("  +  " + f, UIHelper.FONT_SMALL, new Color(180, 210, 255));
            fl.setAlignmentX(JLabel.LEFT_ALIGNMENT);
            inner.add(fl);
            inner.add(javax.swing.Box.createVerticalStrut(8));
        }

        inner.add(javax.swing.Box.createVerticalStrut(30));
        JLabel copy = UIHelper.label("© 2025 Smart Campus System", UIHelper.FONT_SMALL, new Color(140, 180, 230));
        copy.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        inner.add(copy);

        p.add(inner);
        return p;
    }

    // ── Sign In panel ──────────────────────────────────────────────────────
    private JPanel buildSignInPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(java.awt.Color.WHITE);
        p.setBorder(new EmptyBorder(30, 40, 30, 40));

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        JLabel heading = UIHelper.label("Welcome Back", UIHelper.FONT_TITLE, UIHelper.TEXT_PRIMARY);
        heading.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        JLabel sub = UIHelper.label("Sign in to your campus account", UIHelper.FONT_BODY, UIHelper.TEXT_SECONDARY);
        sub.setAlignmentX(JLabel.LEFT_ALIGNMENT);

        JTextField emailField = UIHelper.textField("Email address");
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        JPasswordField passField = UIHelper.passwordField("Password");
        passField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        String[] roles = {"STUDENT", "ORGANIZER", "ADMIN"};
        JComboBox<String> roleBox = new JComboBox<String>(roles);
        roleBox.setFont(UIHelper.FONT_BODY);
        roleBox.setBackground(new Color(245, 248, 255));
        roleBox.setForeground(UIHelper.TEXT_PRIMARY);
        roleBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        JButton signInBtn = UIHelper.primaryButton("Sign In", UIHelper.ACCENT_BLUE);
        signInBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        signInBtn.setAlignmentX(JButton.LEFT_ALIGNMENT);

        JLabel demo = UIHelper.label(
                "Demo: admin@campus.edu / admin123  (ADMIN)   |   events@campus.edu / org123  (ORGANIZER)",
                UIHelper.FONT_SMALL, UIHelper.TEXT_SECONDARY);
        demo.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        signInBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText().trim();
                String pass  = new String(passField.getPassword());
                String role  = (String) roleBox.getSelectedItem();
                try {
                    Person user = authService.login(email, pass, role);
                    JOptionPane.showMessageDialog(LoginFrame.this,
                            "Welcome back, " + user.getName() + "! 👋",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                    openDashboard(user);
                } catch (AuthenticationException ex) {
                    JOptionPane.showMessageDialog(LoginFrame.this, ex.getMessage(),
                            "Login Failed", JOptionPane.ERROR_MESSAGE);
                } catch (SmartCampusException ex) {
                    JOptionPane.showMessageDialog(LoginFrame.this, ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        form.add(heading);
        form.add(javax.swing.Box.createVerticalStrut(4));
        form.add(sub);
        form.add(javax.swing.Box.createVerticalStrut(24));
        form.add(UIHelper.label("Email", UIHelper.FONT_SMALL, UIHelper.TEXT_SECONDARY));
        form.add(javax.swing.Box.createVerticalStrut(4));
        form.add(emailField);
        form.add(javax.swing.Box.createVerticalStrut(14));
        form.add(UIHelper.label("Password", UIHelper.FONT_SMALL, UIHelper.TEXT_SECONDARY));
        form.add(javax.swing.Box.createVerticalStrut(4));
        form.add(passField);
        form.add(javax.swing.Box.createVerticalStrut(14));
        form.add(UIHelper.label("Role", UIHelper.FONT_SMALL, UIHelper.TEXT_SECONDARY));
        form.add(javax.swing.Box.createVerticalStrut(4));
        form.add(roleBox);
        form.add(javax.swing.Box.createVerticalStrut(24));
        form.add(signInBtn);
        form.add(javax.swing.Box.createVerticalStrut(16));
        form.add(demo);

        p.add(form);
        return p;
    }

    // ── Register panel ─────────────────────────────────────────────────────
    private JScrollPane buildRegisterPanel() {
        JPanel p = new JPanel();
        p.setBackground(java.awt.Color.WHITE);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(24, 40, 24, 40));

        JTextField nameF   = UIHelper.textField("Full Name");
        JTextField emailF  = UIHelper.textField("Email address");
        JTextField phoneF  = UIHelper.textField("Phone number");
        JPasswordField passF  = UIHelper.passwordField("Password (min 6 chars)");
        JPasswordField pass2F = UIHelper.passwordField("Confirm Password");

        String[] types = {"STUDENT", "ORGANIZER"};
        JComboBox<String> typeBox = new JComboBox<String>(types);
        typeBox.setFont(UIHelper.FONT_BODY);
        typeBox.setBackground(new Color(245, 248, 255));
        typeBox.setForeground(UIHelper.TEXT_PRIMARY);
        typeBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        JTextField extraF  = UIHelper.textField("Student ID (e.g. STU2024001)");
        JTextField extra2F = UIHelper.textField("Department (e.g. CSE)");
        JTextField extra3F = UIHelper.textField("Year (1-5)");
        JLabel extraLabel  = UIHelper.label("Student ID", UIHelper.FONT_SMALL, UIHelper.TEXT_SECONDARY);
        JLabel extra2Label = UIHelper.label("Department", UIHelper.FONT_SMALL, UIHelper.TEXT_SECONDARY);
        JLabel extra3Label = UIHelper.label("Year", UIHelper.FONT_SMALL, UIHelper.TEXT_SECONDARY);

        Dimension maxW = new Dimension(Integer.MAX_VALUE, 42);
        nameF.setMaximumSize(maxW); emailF.setMaximumSize(maxW); phoneF.setMaximumSize(maxW);
        passF.setMaximumSize(maxW); pass2F.setMaximumSize(maxW);
        extraF.setMaximumSize(maxW); extra2F.setMaximumSize(maxW); extra3F.setMaximumSize(maxW);

        typeBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean isStudent = "STUDENT".equals(typeBox.getSelectedItem());
                extraLabel.setText(isStudent ? "Student ID" : "Organization Name");
                extraF.setText("");
                extra2Label.setVisible(isStudent);
                extra2F.setVisible(isStudent);
                extra3Label.setVisible(isStudent);
                extra3F.setVisible(isStudent);
            }
        });

        JButton createBtn = UIHelper.primaryButton("Create Account", UIHelper.ACCENT_GREEN);
        createBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        createBtn.setAlignmentX(JButton.LEFT_ALIGNMENT);

        createBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name  = nameF.getText().trim();
                String email = emailF.getText().trim();
                String phone = phoneF.getText().trim();
                String pass  = new String(passF.getPassword());
                String pass2 = new String(pass2F.getPassword());
                String type  = (String) typeBox.getSelectedItem();

                if (!pass.equals(pass2)) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    if ("STUDENT".equals(type)) {
                        String sid  = extraF.getText().trim();
                        String dept = extra2F.getText().trim();
                        int year = 1;
                        try { year = Integer.parseInt(extra3F.getText().trim()); } catch (Exception ignored) {}
                        authService.registerStudent(name, email, pass, phone, sid, dept, year);
                    } else {
                        String org = extraF.getText().trim();
                        authService.registerOrganizer(name, email, pass, phone, org);
                    }
                    JOptionPane.showMessageDialog(LoginFrame.this,
                            "Account created! You can now sign in.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    nameF.setText(""); emailF.setText(""); phoneF.setText("");
                    passF.setText(""); pass2F.setText(""); extraF.setText("");
                    extra2F.setText(""); extra3F.setText("");
                } catch (SmartCampusException ex) {
                    JOptionPane.showMessageDialog(LoginFrame.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        addRow(p, "Full Name", nameF);
        addRow(p, "Email", emailF);
        addRow(p, "Phone", phoneF);
        addRow(p, "Password", passF);
        addRow(p, "Confirm Password", pass2F);
        addRow(p, "Account Type", typeBox);
        p.add(extraLabel); p.add(javax.swing.Box.createVerticalStrut(4));
        p.add(extraF);     p.add(javax.swing.Box.createVerticalStrut(12));
        p.add(extra2Label);p.add(javax.swing.Box.createVerticalStrut(4));
        p.add(extra2F);    p.add(javax.swing.Box.createVerticalStrut(12));
        p.add(extra3Label);p.add(javax.swing.Box.createVerticalStrut(4));
        p.add(extra3F);    p.add(javax.swing.Box.createVerticalStrut(20));
        p.add(createBtn);

        return UIHelper.scrollPane(p);
    }

    private void addRow(JPanel p, String labelText, java.awt.Component field) {
        JLabel l = UIHelper.label(labelText, UIHelper.FONT_SMALL, UIHelper.TEXT_SECONDARY);
        l.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        p.add(l);
        p.add(javax.swing.Box.createVerticalStrut(4));
        p.add(field);
        p.add(javax.swing.Box.createVerticalStrut(12));
    }

    private void openDashboard(Person user) {
        if ("ADMIN".equals(user.getRole())) {
            new AdminDashboard(user);
        } else if ("ORGANIZER".equals(user.getRole())) {
            new OrganizerDashboard(user);
        } else {
            new StudentDashboard(user);
        }
    }
}
