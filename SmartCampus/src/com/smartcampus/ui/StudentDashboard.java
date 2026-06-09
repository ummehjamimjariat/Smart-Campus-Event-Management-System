package com.smartcampus.ui;

import com.smartcampus.model.CampusEvent;
import com.smartcampus.model.Person;
import com.smartcampus.model.Registration;
import com.smartcampus.model.Ticket;
import com.smartcampus.service.EventService;
import com.smartcampus.service.TicketService;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class StudentDashboard extends BaseDashboard {

    private final EventService  eventService  = new EventService();
    private final TicketService ticketService = new TicketService();

    // Ticket price per event type
    private static final double PRICE_ACADEMIC = 50.0;
    private static final double PRICE_CULTURAL = 100.0;
    private static final double PRICE_SPORTS   = 80.0;

    public StudentDashboard(Person user) { super(user); }

    protected JButton[] buildSidebarButtons() {
        JButton homeBtn    = sidebarBtn("", "Dashboard");
        JButton eventsBtn  = sidebarBtn("", "Browse Events");
        JButton myRegBtn   = sidebarBtn("", "My Registrations");
        JButton myTktBtn   = sidebarBtn("", "My Tickets");
        JButton profileBtn = sidebarBtn("", "Profile");

        homeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { loadDefaultPanel(); }
        });
        eventsBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { showPanel(buildEventsPanel()); }
        });
        myRegBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { showPanel(buildMyRegsPanel()); }
        });
        myTktBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { showPanel(buildMyTicketsPanel()); }
        });
        profileBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { showPanel(buildProfilePanel()); }
        });
        return new JButton[]{homeBtn, eventsBtn, myRegBtn, myTktBtn, profileBtn};
    }

    public void loadDefaultPanel() { showPanel(buildHomePanel()); }

    // ── Home ───────────────────────────────────────────────────────────────
    private JPanel buildHomePanel() {
        JPanel p = new JPanel(new BorderLayout(0, 20));
        p.setBackground(UIHelper.BG_PAGE);
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        p.add(UIHelper.title("Welcome, " + currentUser.getName()), BorderLayout.NORTH);
        try {
            List<CampusEvent> upcoming = eventService.getUpcomingEvents();
            List<Registration> myRegs  = eventService.getStudentRegistrations(currentUser.getId());
            List<Ticket> myTickets     = ticketService.getTicketsByStudent(currentUser.getId());

            JPanel stats = new JPanel(new GridLayout(1, 3, 16, 0));
            stats.setOpaque(false);
            stats.add(UIHelper.statCard(String.valueOf(upcoming.size()),   "Upcoming Events",  UIHelper.ACCENT_BLUE));
            stats.add(UIHelper.statCard(String.valueOf(myRegs.size()),     "My Registrations", UIHelper.ACCENT_GREEN));
            stats.add(UIHelper.statCard(String.valueOf(myTickets.size()),  "My Tickets",       UIHelper.ACCENT_PURPLE));
            p.add(stats, BorderLayout.CENTER);

            JPanel recent = new JPanel();
            recent.setOpaque(false);
            recent.setLayout(new BoxLayout(recent, BoxLayout.Y_AXIS));
            recent.add(UIHelper.sectionTitle("Recent Upcoming Events"));
            int shown = 0;
            for (CampusEvent ev : upcoming) {
                if (shown++ >= 4) break;
                recent.add(buildEventRow(ev));
                recent.add(javax.swing.Box.createVerticalStrut(8));
            }
            if (upcoming.isEmpty()) recent.add(UIHelper.body("No upcoming events at this time."));
            p.add(recent, BorderLayout.SOUTH);
        } catch (Exception ex) {
            p.add(UIHelper.body("Error: " + ex.getMessage()), BorderLayout.CENTER);
        }
        return p;
    }

    // ── Browse Events ──────────────────────────────────────────────────────
    private JPanel buildEventsPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(UIHelper.BG_PAGE);
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchBar.setOpaque(false);
        JTextField searchField = UIHelper.textField("Search events...");
        searchField.setPreferredSize(new Dimension(220, 36));
        String[] typeOpts = {"ALL", "ACADEMIC", "CULTURAL", "SPORTS"};
        JComboBox<String> typeFilter = new JComboBox<String>(typeOpts);
        typeFilter.setBackground(new Color(245, 248, 255));
        typeFilter.setForeground(UIHelper.TEXT_PRIMARY);
        typeFilter.setFont(UIHelper.FONT_BODY);
        JButton searchBtn = UIHelper.primaryButton("Search", UIHelper.ACCENT_BLUE);
        searchBtn.setPreferredSize(new Dimension(90, 36));
        searchBar.add(UIHelper.label("Browse Events", UIHelper.FONT_H2, UIHelper.TEXT_PRIMARY));
        searchBar.add(searchField);
        searchBar.add(typeFilter);
        searchBar.add(searchBtn);
        p.add(searchBar, BorderLayout.NORTH);

        JPanel listPanel = new JPanel();
        listPanel.setBackground(UIHelper.BG_PAGE);
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        ActionListener doSearch = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                listPanel.removeAll();
                try {
                    String kw   = searchField.getText().trim();
                    String type = (String) typeFilter.getSelectedItem();
                    List<CampusEvent> results = "ALL".equals(type)
                            ? eventService.searchEvents(kw)
                            : eventService.searchEvents(kw, type);
                    if (results.isEmpty()) {
                        listPanel.add(UIHelper.body("No events found."));
                    } else {
                        for (CampusEvent ev : results) {
                            listPanel.add(buildEventCard(ev));
                            listPanel.add(javax.swing.Box.createVerticalStrut(10));
                        }
                    }
                } catch (Exception ex) {
                    listPanel.add(UIHelper.body("Error: " + ex.getMessage()));
                }
                listPanel.revalidate();
                listPanel.repaint();
            }
        };
        searchBtn.addActionListener(doSearch);

        try {
            for (CampusEvent ev : eventService.getUpcomingEvents()) {
                listPanel.add(buildEventCard(ev));
                listPanel.add(javax.swing.Box.createVerticalStrut(10));
            }
        } catch (Exception ex) {
            listPanel.add(UIHelper.body("Error loading events."));
        }
        p.add(UIHelper.scrollPane(listPanel), BorderLayout.CENTER);
        return p;
    }

    // ── My Registrations ──────────────────────────────────────────────────
    private JPanel buildMyRegsPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(UIHelper.BG_PAGE);
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        p.add(UIHelper.sectionTitle("My Registrations"), BorderLayout.NORTH);
        JPanel list = new JPanel();
        list.setBackground(UIHelper.BG_PAGE);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        try {
            List<Registration> regs = eventService.getStudentRegistrations(currentUser.getId());
            if (regs.isEmpty()) {
                list.add(UIHelper.body("You have no active registrations."));
            } else {
                for (Registration r : regs) {
                    CampusEvent ev = eventService.getEventById(r.getEventId());
                    list.add(buildRegCard(r, ev));
                    list.add(javax.swing.Box.createVerticalStrut(8));
                }
            }
        } catch (Exception ex) {
            list.add(UIHelper.body("Error: " + ex.getMessage()));
        }
        p.add(UIHelper.scrollPane(list), BorderLayout.CENTER);
        return p;
    }

    // ── My Tickets ────────────────────────────────────────────────────────
    private JPanel buildMyTicketsPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(UIHelper.BG_PAGE);
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        p.add(UIHelper.sectionTitle("My Tickets"), BorderLayout.NORTH);
        JPanel list = new JPanel();
        list.setBackground(UIHelper.BG_PAGE);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        try {
            List<Ticket> tickets = ticketService.getTicketsByStudent(currentUser.getId());
            if (tickets.isEmpty()) {
                list.add(UIHelper.body("You have no tickets yet. Buy a ticket from Browse Events."));
            } else {
                for (Ticket t : tickets) {
                    list.add(buildTicketCard(t));
                    list.add(javax.swing.Box.createVerticalStrut(10));
                }
            }
        } catch (Exception ex) {
            list.add(UIHelper.body("Error: " + ex.getMessage()));
        }
        p.add(UIHelper.scrollPane(list), BorderLayout.CENTER);
        return p;
    }

    // ── Card builders ──────────────────────────────────────────────────────
    private JPanel buildEventCard(CampusEvent ev) {
        JPanel card = UIHelper.cardPanel();
        card.setLayout(new BorderLayout(10, 6));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titleRow.setOpaque(false);
        titleRow.add(UIHelper.h2(ev.getTitle()));
        titleRow.add(UIHelper.badge(ev.getEventType(), UIHelper.typeColor(ev.getEventType())));
        titleRow.add(UIHelper.badge(ev.getStatus(),    UIHelper.statusColor(ev.getStatus())));
        left.add(titleRow);
        left.add(UIHelper.body("Venue: " + ev.getVenue() + "   Time: " + ev.getDateTime().format(CampusEvent.FORMATTER)));
        left.add(UIHelper.body("Seats: " + ev.getAvailableSeats() + " available   |   Ticket: Tk " + ticketPrice(ev)));
        card.add(left, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        btns.setOpaque(false);

        JButton regBtn = UIHelper.primaryButton("Register", UIHelper.ACCENT_GREEN);
        regBtn.setPreferredSize(new Dimension(100, 34));
        regBtn.setEnabled(ev.isAvailable());
        regBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    eventService.registerStudent(currentUser.getId(), ev.getEventId());
                    JOptionPane.showMessageDialog(StudentDashboard.this,
                            "Registered for: " + ev.getTitle(), "Success", JOptionPane.INFORMATION_MESSAGE);
                    showPanel(buildEventsPanel());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(StudentDashboard.this,
                            ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton buyBtn = UIHelper.primaryButton("Buy Ticket", UIHelper.ACCENT_PURPLE);
        buyBtn.setPreferredSize(new Dimension(110, 34));
        buyBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if (ticketService.hasTicket(currentUser.getId(), ev.getEventId())) {
                        JOptionPane.showMessageDialog(StudentDashboard.this,
                                "You already have a ticket for this event!", "Info", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                    PaymentDialog pd = new PaymentDialog(StudentDashboard.this,
                            currentUser.getId(), ev, ticketPrice(ev));
                    if (pd.getIssuedTicket() != null) {
                        showPanel(buildMyTicketsPanel());
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(StudentDashboard.this,
                            ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btns.add(regBtn);
        btns.add(buyBtn);
        card.add(btns, BorderLayout.EAST);
        return card;
    }

    private double ticketPrice(CampusEvent ev) {
        if ("ACADEMIC".equals(ev.getEventType())) return PRICE_ACADEMIC;
        if ("CULTURAL".equals(ev.getEventType())) return PRICE_CULTURAL;
        return PRICE_SPORTS;
    }

    private JPanel buildRegCard(Registration r, CampusEvent ev) {
        JPanel card = UIHelper.cardPanel();
        card.setLayout(new BorderLayout(10, 6));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        String evTitle = ev != null ? ev.getTitle() : r.getEventId();
        String evVenue = ev != null ? ev.getVenue() : "N/A";
        left.add(UIHelper.h2(evTitle));
        left.add(UIHelper.body("Venue: " + evVenue + "   Reg ID: " + r.getRegistrationId()));
        left.add(UIHelper.badge(r.getStatus(), UIHelper.statusColor(r.getStatus())));
        card.add(left, BorderLayout.CENTER);
        JButton cancelBtn = UIHelper.primaryButton("Cancel", UIHelper.ACCENT_RED);
        cancelBtn.setPreferredSize(new Dimension(90, 34));
        cancelBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int ok = JOptionPane.showConfirmDialog(StudentDashboard.this,
                        "Cancel registration for: " + evTitle + "?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (ok == JOptionPane.YES_OPTION) {
                    try {
                        eventService.cancelRegistration(currentUser.getId(), r.getEventId());
                        showPanel(buildMyRegsPanel());
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(StudentDashboard.this,
                                ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        card.add(cancelBtn, BorderLayout.EAST);
        return card;
    }

    private JPanel buildTicketCard(Ticket t) {
        JPanel card = UIHelper.cardPanel();
        card.setLayout(new BorderLayout(10, 6));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        row1.setOpaque(false);
        row1.add(UIHelper.h2(t.getEventTitle()));
        Color methodColor = "bKash".equals(t.getPaymentMethod()) ? new Color(220,0,80)
                : "Rocket".equals(t.getPaymentMethod()) ? new Color(130,40,180)
                : new Color(230,100,0);
        row1.add(UIHelper.badge(t.getPaymentMethod(), methodColor));
        row1.add(UIHelper.badge(t.getStatus(), UIHelper.statusColor(t.getStatus())));
        left.add(row1);
        left.add(UIHelper.body("Ticket ID: " + t.getTicketId()
                + "   TrxID: " + t.getTransactionId()));
        left.add(UIHelper.body("Paid: Tk " + String.format("%.2f", t.getAmount())
                + "   From: " + t.getPayerNumber()
                + "   At: " + t.getPaidAt().format(Ticket.FORMATTER)));
        card.add(left, BorderLayout.CENTER);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        right.setOpaque(false);
        if (Ticket.STATUS_CONFIRMED.equals(t.getStatus())) {
            JButton cancelBtn = UIHelper.primaryButton("Cancel", UIHelper.ACCENT_RED);
            cancelBtn.setPreferredSize(new Dimension(90, 34));
            cancelBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int ok = JOptionPane.showConfirmDialog(StudentDashboard.this,
                            "Cancel ticket " + t.getTicketId() + "?", "Confirm", JOptionPane.YES_NO_OPTION);
                    if (ok == JOptionPane.YES_OPTION) {
                        try {
                            ticketService.cancelTicket(t.getTicketId());
                            showPanel(buildMyTicketsPanel());
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(StudentDashboard.this,
                                    ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            });
            right.add(cancelBtn);
        }
        card.add(right, BorderLayout.EAST);
        return card;
    }

    private JPanel buildEventRow(CampusEvent ev) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        row.add(UIHelper.badge(ev.getEventType(), UIHelper.typeColor(ev.getEventType())), BorderLayout.WEST);
        row.add(UIHelper.label(ev.getTitle() + "  —  " + ev.getVenue(),
                UIHelper.FONT_BODY, UIHelper.TEXT_PRIMARY), BorderLayout.CENTER);
        row.add(UIHelper.label(ev.getDateTime().format(CampusEvent.FORMATTER),
                UIHelper.FONT_SMALL, UIHelper.TEXT_SECONDARY), BorderLayout.EAST);
        return row;
    }
}
