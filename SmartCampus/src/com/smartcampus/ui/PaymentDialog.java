package com.smartcampus.ui;

import com.smartcampus.model.CampusEvent;
import com.smartcampus.model.Ticket;
import com.smartcampus.service.TicketService;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class PaymentDialog extends JDialog {

    // ── bKash brand colours ──────────────────────────────────────────────────
    private static final Color BKASH_PINK   = new Color(220, 0,  80);
    private static final Color BKASH_DARK   = new Color(160, 0,  55);
    private static final Color ROCKET_PURPLE= new Color(130, 40, 180);
    private static final Color ROCKET_DARK  = new Color(90,  20, 130);
    private static final Color NAGAD_ORANGE = new Color(230, 100, 0);
    private static final Color NAGAD_DARK   = new Color(180, 70,  0);
    private static final Color WHITE        = Color.WHITE;
    private static final Color LIGHT_GRAY   = new Color(245, 245, 245);
    private static final Color BORDER_GRAY  = new Color(220, 220, 220);
    private static final Color TEXT_DARK    = new Color(30,  30,  30);
    private static final Color TEXT_MID     = new Color(100, 100, 100);
    private static final Color SUCCESS_GREEN= new Color(0,  180,  80);

    private final String     studentId;
    private final CampusEvent event;
    private final double     ticketPrice;
    private final TicketService ticketService = new TicketService();

    private String selectedMethod = Ticket.PAY_BKASH; // default
    private Ticket issuedTicket   = null;
    private CardLayout cardLayout;
    private JPanel     cards;

    // pages
    private static final String PAGE_SELECT  = "SELECT";
    private static final String PAGE_BKASH   = "BKASH";
    private static final String PAGE_ROCKET  = "ROCKET";
    private static final String PAGE_NAGAD   = "NAGAD";
    private static final String PAGE_PROCESS = "PROCESS";
    private static final String PAGE_SUCCESS = "SUCCESS";

    public PaymentDialog(JFrame parent, String studentId, CampusEvent event, double ticketPrice) {
        super(parent, "Payment Gateway", true);
        this.studentId   = studentId;
        this.event       = event;
        this.ticketPrice = ticketPrice;
        setSize(420, 580);
        setLocationRelativeTo(parent);
        setResizable(false);
        buildUI();
        setVisible(true);
    }

    private void buildUI() {
        cardLayout = new CardLayout();
        cards      = new JPanel(cardLayout);
        cards.add(buildSelectPage(),  PAGE_SELECT);
        cards.add(buildPayPage(Ticket.PAY_BKASH,  BKASH_PINK,   BKASH_DARK),  PAGE_BKASH);
        cards.add(buildPayPage(Ticket.PAY_ROCKET, ROCKET_PURPLE, ROCKET_DARK), PAGE_ROCKET);
        cards.add(buildPayPage(Ticket.PAY_NAGAD,  NAGAD_ORANGE,  NAGAD_DARK),  PAGE_NAGAD);
        cards.add(buildProcessPage(), PAGE_PROCESS);
        cards.add(buildSuccessPage(), PAGE_SUCCESS);
        setContentPane(cards);
        cardLayout.show(cards, PAGE_SELECT);
    }

    // ════════════════════════════════════════════════════════════════════════
    // PAGE 1 — Choose payment method
    // ════════════════════════════════════════════════════════════════════════
    private JPanel buildSelectPage() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(WHITE);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(30, 30, 30));
        header.setBorder(new EmptyBorder(16, 20, 16, 20));
        JLabel title = new JLabel("Select Payment Method");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(WHITE);
        JLabel amt = new JLabel("Tk " + String.format("%.0f", ticketPrice));
        amt.setFont(new Font("Segoe UI", Font.BOLD, 20));
        amt.setForeground(new Color(255, 215, 0));
        header.add(title, BorderLayout.WEST);
        header.add(amt,   BorderLayout.EAST);
        p.add(header, BorderLayout.NORTH);

        // Event info strip
        JPanel info = new JPanel(new GridLayout(2, 1, 0, 2));
        info.setBackground(LIGHT_GRAY);
        info.setBorder(new EmptyBorder(10, 20, 10, 20));
        JLabel evTitle = new JLabel("Event: " + event.getTitle());
        evTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        evTitle.setForeground(TEXT_DARK);
        JLabel evVenue = new JLabel("Venue: " + event.getVenue() + "   |   " + event.getDateTime().format(CampusEvent.FORMATTER));
        evVenue.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        evVenue.setForeground(TEXT_MID);
        info.add(evTitle);
        info.add(evVenue);
        p.add(info, BorderLayout.CENTER);

        // Method buttons
        JPanel methods = new JPanel();
        methods.setBackground(WHITE);
        methods.setLayout(new BoxLayout(methods, BoxLayout.Y_AXIS));
        methods.setBorder(new EmptyBorder(24, 30, 10, 30));

        JLabel choose = new JLabel("Choose your payment method:");
        choose.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        choose.setForeground(TEXT_MID);
        choose.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        methods.add(choose);
        methods.add(javax.swing.Box.createVerticalStrut(16));

        methods.add(methodButton("bKash",  "Mobile Banking", BKASH_PINK,    PAGE_BKASH));
        methods.add(javax.swing.Box.createVerticalStrut(12));
        methods.add(methodButton("Rocket", "Mobile Banking", ROCKET_PURPLE, PAGE_ROCKET));
        methods.add(javax.swing.Box.createVerticalStrut(12));
        methods.add(methodButton("Nagad",  "Mobile Banking", NAGAD_ORANGE,  PAGE_NAGAD));
        methods.add(javax.swing.Box.createVerticalStrut(24));

        // Cancel
        JButton cancel = plainButton("Cancel", TEXT_MID);
        cancel.setAlignmentX(JButton.CENTER_ALIGNMENT);
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { dispose(); }
        });
        methods.add(cancel);

        p.add(methods, BorderLayout.SOUTH);
        return p;
    }

    private JButton methodButton(String name, String sub, Color color, String page) {
        JButton btn = new JButton() {
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? color.darker() : WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(color);
                g2.setStroke(new java.awt.BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 12, 12);
                // colour circle
                g2.setColor(color);
                g2.fillOval(14, getHeight()/2-16, 32, 32);
                // initials
                g2.setColor(WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                String init = name.substring(0,1);
                g2.drawString(init, 24, getHeight()/2+5);
                // name
                g2.setColor(getModel().isRollover() ? WHITE : TEXT_DARK);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                g2.drawString(name, 60, getHeight()/2-2);
                g2.setColor(TEXT_MID);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                g2.drawString(sub, 60, getHeight()/2+14);
                // arrow
                g2.setColor(color);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
                g2.drawString(">", getWidth()-28, getHeight()/2+7);
            }
        };
        btn.setPreferredSize(new Dimension(340, 64));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectedMethod = name;
                cardLayout.show(cards, page);
            }
        });
        return btn;
    }

    // ════════════════════════════════════════════════════════════════════════
    // PAGE 2 — bKash / Rocket / Nagad payment form
    // ════════════════════════════════════════════════════════════════════════
    private JPanel buildPayPage(String method, Color primary, Color dark) {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(WHITE);

        // ── Top brand bar ─────────────────────────────────────────────────
        JPanel topBar = new JPanel() {
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0,0,primary,getWidth(),getHeight(),dark));
                g2.fillRect(0,0,getWidth(),getHeight());
            }
        };
        topBar.setOpaque(false);
        topBar.setPreferredSize(new Dimension(0, 90));
        topBar.setLayout(new BorderLayout());
        topBar.setBorder(new EmptyBorder(12, 16, 12, 16));

        JLabel backBtn = new JLabel("< Back");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        backBtn.setForeground(new Color(255,255,255,180));
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                cardLayout.show(cards, PAGE_SELECT);
            }
        });

        JPanel brandCenter = new JPanel();
        brandCenter.setOpaque(false);
        brandCenter.setLayout(new BoxLayout(brandCenter, BoxLayout.Y_AXIS));
        JLabel brandName = new JLabel(method, SwingConstants.CENTER);
        brandName.setFont(new Font("Segoe UI", Font.BOLD, 26));
        brandName.setForeground(WHITE);
        brandName.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        JLabel brandSub = new JLabel("Mobile Financial Service", SwingConstants.CENTER);
        brandSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        brandSub.setForeground(new Color(255,255,255,200));
        brandSub.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        brandCenter.add(brandName);
        brandCenter.add(brandSub);

        JLabel amtLabel = new JLabel("Tk " + String.format("%.0f", ticketPrice));
        amtLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        amtLabel.setForeground(new Color(255,255,180));

        topBar.add(backBtn,   BorderLayout.WEST);
        topBar.add(brandCenter, BorderLayout.CENTER);
        topBar.add(amtLabel,  BorderLayout.EAST);
        root.add(topBar, BorderLayout.NORTH);

        // ── Form ──────────────────────────────────────────────────────────
        JPanel form = new JPanel();
        form.setBackground(WHITE);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(20, 28, 10, 28));

        // Merchant number (read-only)
        form.add(formLabel("Merchant Number"));
        form.add(javax.swing.Box.createVerticalStrut(4));
        JLabel merchantNum = new JLabel("01XXXXXXXXX  (Smart Campus EMS)");
        merchantNum.setFont(new Font("Segoe UI", Font.BOLD, 13));
        merchantNum.setForeground(primary);
        merchantNum.setBorder(new EmptyBorder(0, 4, 0, 0));
        form.add(merchantNum);
        form.add(javax.swing.Box.createVerticalStrut(14));

        // Your mobile number
        form.add(formLabel("Your " + method + " Number"));
        form.add(javax.swing.Box.createVerticalStrut(4));
        JTextField numField = styledField("01XXXXXXXXXX");
        numField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        form.add(numField);
        form.add(javax.swing.Box.createVerticalStrut(14));

        // Amount (read-only display)
        form.add(formLabel("Amount"));
        form.add(javax.swing.Box.createVerticalStrut(4));
        JTextField amtField = styledField("");
        amtField.setText("Tk " + String.format("%.2f", ticketPrice));
        amtField.setEditable(false);
        amtField.setBackground(LIGHT_GRAY);
        amtField.setFont(new Font("Segoe UI", Font.BOLD, 14));
        amtField.setForeground(primary);
        amtField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        form.add(amtField);
        form.add(javax.swing.Box.createVerticalStrut(14));

        // Reference
        form.add(formLabel("Reference (optional)"));
        form.add(javax.swing.Box.createVerticalStrut(4));
        JTextField refField = styledField("e.g. Event Ticket for " + event.getTitle());
        refField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        form.add(refField);
        form.add(javax.swing.Box.createVerticalStrut(14));

        // PIN
        form.add(formLabel(method + " PIN"));
        form.add(javax.swing.Box.createVerticalStrut(4));
        JPasswordField pinField = new JPasswordField();
        pinField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        pinField.setForeground(TEXT_DARK);
        pinField.setBackground(WHITE);
        pinField.setCaretColor(primary);
        pinField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        pinField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_GRAY, 1, true),
                new EmptyBorder(8, 12, 8, 12)));
        form.add(pinField);
        form.add(javax.swing.Box.createVerticalStrut(6));

        JLabel pinNote = new JLabel("  * PIN is not stored or transmitted anywhere");
        pinNote.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        pinNote.setForeground(TEXT_MID);
        form.add(pinNote);
        form.add(javax.swing.Box.createVerticalStrut(20));

        // Pay button
        JButton payBtn = new JButton("Confirm & Pay Tk " + String.format("%.0f", ticketPrice)) {
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0,0,primary,0,getHeight(),dark));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                g2.setColor(WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                java.awt.FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
            }
        };
        payBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        payBtn.setOpaque(false);
        payBtn.setContentAreaFilled(false);
        payBtn.setBorderPainted(false);
        payBtn.setFocusPainted(false);
        payBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        payBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String num = numField.getText().trim();
                String pin = new String(pinField.getPassword()).trim();
                String ref = refField.getText().trim();
                if (num.isEmpty() || num.equals("01XXXXXXXXXX")) {
                    shake(numField); return;
                }
                if (num.length() < 11) {
                    numField.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                    numField.setToolTipText("Enter a valid 11-digit number");
                    return;
                }
                if (pin.isEmpty()) { shake(pinField); return; }
                if (ref.isEmpty()) ref = "Ticket for " + event.getTitle();
                processPayment(method, num, ref);
            }
        });

        form.add(payBtn);

        // Security strip
        form.add(javax.swing.Box.createVerticalStrut(12));
        JLabel secure = new JLabel("  Secured by SSL Encryption  |  " + method + " Verified");
        secure.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        secure.setForeground(TEXT_MID);
        secure.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        form.add(secure);

        root.add(form, BorderLayout.CENTER);
        return root;
    }

    // ════════════════════════════════════════════════════════════════════════
    // PAGE 3 — Processing animation
    // ════════════════════════════════════════════════════════════════════════
    private JPanel buildProcessPage() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(WHITE);
        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));

        JLabel spinner = new JLabel("...", SwingConstants.CENTER);
        spinner.setFont(new Font("Segoe UI", Font.BOLD, 48));
        spinner.setForeground(BKASH_PINK);
        spinner.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        JLabel msg = new JLabel("Processing Payment...", SwingConstants.CENTER);
        msg.setFont(new Font("Segoe UI", Font.BOLD, 16));
        msg.setForeground(TEXT_DARK);
        msg.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Please wait, do not close this window", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(TEXT_MID);
        sub.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        inner.add(spinner);
        inner.add(javax.swing.Box.createVerticalStrut(16));
        inner.add(msg);
        inner.add(javax.swing.Box.createVerticalStrut(8));
        inner.add(sub);
        p.add(inner);
        return p;
    }

    // ════════════════════════════════════════════════════════════════════════
    // PAGE 4 — Success / receipt
    // ════════════════════════════════════════════════════════════════════════
    private JPanel buildSuccessPage() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(WHITE);
        p.setName("SUCCESS_PAGE");
        return p;
    }

    private void showSuccessPage(Ticket ticket) {
        // Find and rebuild the success page
        JPanel successPage = new JPanel(new BorderLayout());
        successPage.setBackground(WHITE);

        // Green header
        JPanel header = new JPanel() {
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0,0,SUCCESS_GREEN,0,getHeight(),SUCCESS_GREEN.darker()));
                g2.fillRect(0,0,getWidth(),getHeight());
            }
        };
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 160));
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(16, 0, 16, 0));

        JLabel tick = new JLabel("[ OK ]", SwingConstants.CENTER);
        tick.setFont(new Font("Segoe UI", Font.BOLD, 20));
        tick.setForeground(WHITE);
        tick.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        JLabel done = new JLabel("Payment Successful!", SwingConstants.CENTER);
        done.setFont(new Font("Segoe UI", Font.BOLD, 18));
        done.setForeground(WHITE);
        done.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        JLabel amtL = new JLabel("Tk " + String.format("%.2f", ticket.getAmount()), SwingConstants.CENTER);
        amtL.setFont(new Font("Segoe UI", Font.BOLD, 30));
        amtL.setForeground(WHITE);
        amtL.setOpaque(true);
        amtL.setBackground(new Color(0, 0, 0, 0));
        amtL.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        header.add(tick);
        header.add(done);
        header.add(amtL);
        successPage.add(header, BorderLayout.NORTH);

        // Receipt body
        JPanel receipt = new JPanel();
        receipt.setBackground(WHITE);
        receipt.setLayout(new BoxLayout(receipt, BoxLayout.Y_AXIS));
        receipt.setBorder(new EmptyBorder(16, 28, 16, 28));

        receipt.add(receiptTitle("PAYMENT RECEIPT"));
        receipt.add(javax.swing.Box.createVerticalStrut(12));
        receipt.add(receiptRow("Ticket ID",        ticket.getTicketId()));
        receipt.add(receiptRow("Transaction ID",   ticket.getTransactionId()));
        receipt.add(receiptRow("Payment Method",   ticket.getPaymentMethod()));
        receipt.add(receiptRow("Paid From",        ticket.getPayerNumber()));
        receipt.add(receiptRow("Event",            ticket.getEventTitle()));
        receipt.add(receiptRow("Venue",            event.getVenue()));
        receipt.add(receiptRow("Date & Time",      event.getDateTime().format(CampusEvent.FORMATTER)));
        receipt.add(receiptRow("Amount Paid",      "Tk " + String.format("%.2f", ticket.getAmount())));
        receipt.add(receiptRow("Reference",        ticket.getReference()));
        receipt.add(receiptRow("Status",           ticket.getStatus()));
        receipt.add(receiptRow("Paid At",          ticket.getPaidAt().format(Ticket.FORMATTER)));
        receipt.add(javax.swing.Box.createVerticalStrut(16));

        // Dashed line
        JLabel dash = new JLabel("- - - - - - - - - - - - - - - - - - - -");
        dash.setFont(new Font("Courier New", Font.PLAIN, 11));
        dash.setForeground(new Color(180,180,180));
        dash.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        receipt.add(dash);
        receipt.add(javax.swing.Box.createVerticalStrut(8));

        JLabel note = new JLabel("Show this ticket at the event entrance");
        note.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        note.setForeground(TEXT_MID);
        note.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        receipt.add(note);
        receipt.add(javax.swing.Box.createVerticalStrut(16));

        JButton closeBtn = new JButton("Done") {
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(SUCCESS_GREEN);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                g2.setColor(WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                java.awt.FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2,
                        (getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        closeBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        closeBtn.setOpaque(false);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.setAlignmentX(JButton.CENTER_ALIGNMENT);
        closeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { dispose(); }
        });
        receipt.add(closeBtn);

        successPage.add(UIHelper.scrollPane(receipt), BorderLayout.CENTER);

        // Replace old success panel
        cards.add(successPage, PAGE_SUCCESS);
        cardLayout.show(cards, PAGE_SUCCESS);
    }

    // ════════════════════════════════════════════════════════════════════════
    // Payment processing
    // ════════════════════════════════════════════════════════════════════════
    private void processPayment(String method, String number, String reference) {
        cardLayout.show(cards, PAGE_PROCESS);

        // Simulate network delay like real mobile banking
        Timer timer = new Timer(2500, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    issuedTicket = ticketService.issueTicket(
                            studentId, event.getEventId(), event.getTitle(),
                            ticketPrice, method, number, reference);
                    showSuccessPage(issuedTicket);
                } catch (Exception ex) {
                    cardLayout.show(cards, PAGE_SELECT);
                    javax.swing.JOptionPane.showMessageDialog(PaymentDialog.this,
                            "Payment failed: " + ex.getMessage(),
                            "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    // ════════════════════════════════════════════════════════════════════════
    // Helper builders
    // ════════════════════════════════════════════════════════════════════════
    private JLabel formLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setForeground(TEXT_MID);
        l.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        return l;
    }

    private JTextField styledField(String placeholder) {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setForeground(TEXT_DARK);
        f.setBackground(WHITE);
        f.setCaretColor(BKASH_PINK);
        f.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_GRAY, 1, true),
                new EmptyBorder(8, 12, 8, 12)));
        f.setText(placeholder);
        f.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (f.getText().equals(placeholder)) f.setText("");
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (f.getText().isEmpty()) f.setText(placeholder);
            }
        });
        return f;
    }

    private JButton plainButton(String text, Color color) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        b.setForeground(color);
        b.setBackground(WHITE);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JPanel receiptRow(String key, String value) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        JLabel k = new JLabel(key);
        k.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        k.setForeground(TEXT_MID);
        JLabel v = new JLabel(value != null ? value : "-");
        v.setFont(new Font("Segoe UI", Font.BOLD, 11));
        v.setForeground(TEXT_DARK);
        row.add(k, BorderLayout.WEST);
        row.add(v, BorderLayout.EAST);
        return row;
    }

    private JLabel receiptTitle(String text) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(new Font("Courier New", Font.BOLD, 13));
        l.setForeground(TEXT_DARK);
        l.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        return l;
    }

    private void shake(java.awt.Component comp) {
        Color orig = comp.getBackground();
        comp.setBackground(new Color(255, 220, 220));
        Timer t = new Timer(300, new ActionListener() {
            public void actionPerformed(ActionEvent e) { comp.setBackground(orig); }
        });
        t.setRepeats(false);
        t.start();
    }

    public Ticket getIssuedTicket() { return issuedTicket; }
}
