package com.smartcampus.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

public class UIHelper {

    // ── PROFESSIONAL BLUE & WHITE THEME ───────────────────────────────────
    // Page background: clean light grey-blue
    public static final Color BG_PAGE      = new Color(241, 245, 252);
    // Sidebar: deep royal blue
    public static final Color BG_SIDEBAR   = new Color(26,  58,  110);
    // Sidebar hover
    public static final Color BG_SIDEBAR_HOVER = new Color(40,  80,  150);
    // Card background: pure white
    public static final Color BG_CARD      = new Color(255, 255, 255);
    // Top bar: white
    public static final Color BG_TOPBAR    = new Color(255, 255, 255);
    // Border: light blue-grey
    public static final Color BORDER_COLOR = new Color(210, 220, 240);
    // Primary text: very dark navy
    public static final Color TEXT_PRIMARY = new Color(12,  28,  64);
    // Secondary text: medium blue-grey
    public static final Color TEXT_SECONDARY = new Color(85, 105, 145);
    // Sidebar text: white
    public static final Color SIDEBAR_TEXT  = new Color(200, 215, 245);
    // Sidebar text active: bright white
    public static final Color SIDEBAR_TEXT_ACTIVE = new Color(255, 255, 255);

    // ── ACCENT COLOURS ─────────────────────────────────────────────────────
    public static final Color ACCENT_BLUE   = new Color(30,  100, 210);
    public static final Color ACCENT_GREEN  = new Color(16,  148, 87);
    public static final Color ACCENT_PURPLE = new Color(112, 55,  195);
    public static final Color ACCENT_ORANGE = new Color(225, 115, 20);
    public static final Color ACCENT_RED    = new Color(196, 38,  38);
    public static final Color ACCENT_TEAL   = new Color(13,  148, 148);

    // ── FONTS ──────────────────────────────────────────────────────────────
    public static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD,  22);
    public static final Font FONT_H2     = new Font("Segoe UI", Font.BOLD,  15);
    public static final Font FONT_BODY   = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BOLD   = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font FONT_MONO   = new Font("Consolas",  Font.PLAIN, 12);

    // Keep BG_DARK as alias for BG_PAGE so existing code compiles
    public static final Color BG_DARK = BG_PAGE;

    // ── COLOUR HELPERS ─────────────────────────────────────────────────────
    public static Color typeColor(String type) {
        if ("ACADEMIC".equals(type))  return ACCENT_BLUE;
        if ("CULTURAL".equals(type))  return ACCENT_PURPLE;
        if ("SPORTS".equals(type))    return ACCENT_GREEN;
        if ("ADMIN".equals(type))     return ACCENT_RED;
        if ("ORGANIZER".equals(type)) return ACCENT_ORANGE;
        if ("STUDENT".equals(type))   return ACCENT_BLUE;
        return TEXT_SECONDARY;
    }

    public static Color statusColor(String status) {
        if ("UPCOMING".equals(status))  return ACCENT_GREEN;
        if ("ONGOING".equals(status))   return ACCENT_ORANGE;
        if ("COMPLETED".equals(status)) return TEXT_SECONDARY;
        if ("CANCELLED".equals(status)) return ACCENT_RED;
        return TEXT_SECONDARY;
    }

    // ── LABELS ─────────────────────────────────────────────────────────────
    public static JLabel label(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        return l;
    }

    public static JLabel title(String text) { return label(text, FONT_TITLE, TEXT_PRIMARY); }
    public static JLabel h2(String text)    { return label(text, FONT_H2,    TEXT_PRIMARY); }
    public static JLabel body(String text)  { return label(text, FONT_BODY,  TEXT_SECONDARY); }

    public static JLabel badge(String text, Color color) {
        JLabel l = new JLabel(" " + text + " ") {
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 25));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 160));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                super.paintComponent(g);
            }
        };
        l.setFont(new Font("Segoe UI", Font.BOLD, 10));
        l.setForeground(color);
        l.setOpaque(false);
        return l;
    }

    // ── TEXT FIELDS ────────────────────────────────────────────────────────
    public static JTextField textField(String placeholder) {
        JTextField f = new JTextField() {
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(TEXT_SECONDARY);
                    g2.setFont(FONT_BODY);
                    Insets ins = getInsets();
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(placeholder, ins.left + 2, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                }
            }
        };
        styleInput(f);
        return f;
    }

    public static JPasswordField passwordField(String placeholder) {
        JPasswordField f = new JPasswordField();
        styleInput(f);
        return f;
    }

    private static void styleInput(javax.swing.text.JTextComponent f) {
        f.setFont(FONT_BODY);
        f.setForeground(TEXT_PRIMARY);
        f.setBackground(Color.WHITE);
        f.setCaretColor(ACCENT_BLUE);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(8, 12, 8, 12)));
    }

    public static JTextArea textArea() {
        JTextArea a = new JTextArea();
        a.setFont(FONT_BODY);
        a.setForeground(TEXT_PRIMARY);
        a.setBackground(Color.WHITE);
        a.setCaretColor(ACCENT_BLUE);
        a.setLineWrap(true);
        a.setWrapStyleWord(true);
        a.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                new EmptyBorder(8, 12, 8, 12)));
        return a;
    }

    // ── BUTTONS ────────────────────────────────────────────────────────────
    public static JButton primaryButton(String text, Color color) {
        JButton b = new JButton(text) {
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color base = getModel().isRollover() ? color.brighter() : color;
                g2.setColor(base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(Color.WHITE);
                g2.setFont(FONT_BOLD);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth()  - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
            }
        };
        b.setPreferredSize(new Dimension(160, 38));
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    public static JButton sidebarButton(String icon, String text) {
        JButton b = new JButton(text) {
            private boolean hover = false;
            { addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent e) { hover = true;  repaint(); }
                public void mouseExited (java.awt.event.MouseEvent e) { hover = false; repaint(); }
            }); }
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (hover) {
                    g2.setColor(BG_SIDEBAR_HOVER);
                    g2.fillRoundRect(6, 2, getWidth()-12, getHeight()-4, 8, 8);
                }
                // left accent bar on hover
                if (hover) {
                    g2.setColor(new Color(120, 180, 255));
                    g2.fillRoundRect(6, 6, 3, getHeight()-12, 3, 3);
                }
                g2.setColor(hover ? SIDEBAR_TEXT_ACTIVE : SIDEBAR_TEXT);
                g2.setFont(FONT_BODY);
                FontMetrics fm = g2.getFontMetrics();
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), 20, y);
            }
        };
        b.setPreferredSize(new Dimension(200, 44));
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ── PANELS ─────────────────────────────────────────────────────────────
    public static JPanel darkPanel() {
        JPanel p = new JPanel();
        p.setBackground(BG_PAGE);
        return p;
    }

    public static JPanel cardPanel() {
        JPanel p = new JPanel() {
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
            }
        };
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(14, 16, 14, 16));
        return p;
    }

    public static JScrollPane scrollPane(java.awt.Component view) {
        JScrollPane sp = new JScrollPane(view);
        sp.setBackground(BG_PAGE);
        sp.getViewport().setBackground(BG_PAGE);
        sp.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        sp.getVerticalScrollBar().setUnitIncrement(16);
        return sp;
    }

    public static JLabel sectionTitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_TITLE);
        l.setForeground(TEXT_PRIMARY);
        l.setBorder(new EmptyBorder(0, 0, 14, 0));
        return l;
    }

    public static JPanel statCard(String value, String labelText, Color color) {
        JPanel card = new JPanel() {
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // white card with colored top border
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth(), 6, 4, 4);
                g2.fillRect(0, 3, getWidth(), 6);
                g2.setColor(BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
            }
        };
        card.setOpaque(false);
        card.setLayout(new java.awt.BorderLayout(0, 6));
        card.setBorder(new EmptyBorder(20, 16, 16, 16));

        JLabel val = new JLabel(value, javax.swing.SwingConstants.CENTER);
        val.setFont(new Font("Segoe UI", Font.BOLD, 34));
        val.setForeground(color);

        JLabel lbl = new JLabel(labelText, javax.swing.SwingConstants.CENTER);
        lbl.setFont(FONT_SMALL);
        lbl.setForeground(TEXT_SECONDARY);

        card.add(val, java.awt.BorderLayout.CENTER);
        card.add(lbl, java.awt.BorderLayout.SOUTH);
        return card;
    }
}
