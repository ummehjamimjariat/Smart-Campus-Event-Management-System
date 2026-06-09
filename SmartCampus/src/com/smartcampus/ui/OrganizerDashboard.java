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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class OrganizerDashboard extends BaseDashboard {

    private final EventService  eventService  = new EventService();
    private final TicketService ticketService = new TicketService();

    public OrganizerDashboard(Person user) { super(user); }

    protected JButton[] buildSidebarButtons() {
        JButton homeBtn    = sidebarBtn("", "Dashboard");
        JButton createBtn  = sidebarBtn("", "Create Event");
        JButton myEvtsBtn  = sidebarBtn("", "My Events");
        JButton ticketsBtn = sidebarBtn("", "Event Tickets");
        JButton profileBtn = sidebarBtn("", "Profile");

        homeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { loadDefaultPanel(); }
        });
        createBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { showPanel(buildCreatePanel()); }
        });
        myEvtsBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { showPanel(buildMyEventsPanel()); }
        });
        ticketsBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { showPanel(buildEventTicketsPanel()); }
        });
        profileBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { showPanel(buildProfilePanel()); }
        });
        return new JButton[]{homeBtn, createBtn, myEvtsBtn, profileBtn};
    }

    public void loadDefaultPanel() { showPanel(buildHomePanel()); }

    // ── Home ───────────────────────────────────────────────────────────────
    private JPanel buildHomePanel() {
        JPanel p = new JPanel(new BorderLayout(0, 20));
        p.setBackground(UIHelper.BG_PAGE);
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        p.add(UIHelper.title("Organizer Dashboard 🎭"), BorderLayout.NORTH);
        try {
            List<CampusEvent> myEvents = eventService.getEventsByOrganizer(currentUser.getId());
            long upcoming = 0, cancelled = 0, completed = 0;
            for (CampusEvent e : myEvents) {
                if ("UPCOMING".equals(e.getStatus()))  upcoming++;
                if ("CANCELLED".equals(e.getStatus())) cancelled++;
                if ("COMPLETED".equals(e.getStatus())) completed++;
            }
            JPanel stats = new JPanel(new GridLayout(1, 3, 16, 0));
            stats.setOpaque(false);
            stats.add(UIHelper.statCard(String.valueOf(myEvents.size()), "Total My Events",   UIHelper.ACCENT_BLUE));
            stats.add(UIHelper.statCard(String.valueOf(upcoming),        "Upcoming",           UIHelper.ACCENT_GREEN));
            stats.add(UIHelper.statCard(String.valueOf(completed),       "Completed",          UIHelper.ACCENT_PURPLE));
            p.add(stats, BorderLayout.CENTER);
        } catch (Exception ex) {
            p.add(UIHelper.body("Error: " + ex.getMessage()), BorderLayout.CENTER);
        }
        return p;
    }

    // ── Create Event ───────────────────────────────────────────────────────
    private JPanel buildCreatePanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(UIHelper.BG_PAGE);
        outer.setBorder(new EmptyBorder(10, 10, 10, 10));
        outer.add(UIHelper.sectionTitle("➕ Create New Event"), BorderLayout.NORTH);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(10, 0, 10, 0));

        JTextField titleF = UIHelper.textField("Event Title");
        JTextArea  descF  = UIHelper.textArea();
        descF.setRows(3);
        JTextField venueF = UIHelper.textField("Venue");
        JTextField dateF  = UIHelper.textField("Date & Time (yyyy-MM-dd HH:mm)");
        JTextField capF   = UIHelper.textField("Max Capacity");
        JTextField extraF = UIHelper.textField("Extra (Department / Theme / Sport Type)");

        String[] types = {"ACADEMIC", "CULTURAL", "SPORTS"};
        JComboBox<String> typeBox = new JComboBox<String>(types);
        typeBox.setBackground(java.awt.Color.WHITE);
        typeBox.setForeground(UIHelper.TEXT_PRIMARY);
        typeBox.setFont(UIHelper.FONT_BODY);

        Dimension maxW = new Dimension(Integer.MAX_VALUE, 40);
        titleF.setMaximumSize(maxW); venueF.setMaximumSize(maxW);
        dateF.setMaximumSize(maxW);  capF.setMaximumSize(maxW);
        extraF.setMaximumSize(maxW); typeBox.setMaximumSize(maxW);
        descF.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JButton createBtn = UIHelper.primaryButton("Create Event", UIHelper.ACCENT_GREEN);
        createBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        createBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String title = titleF.getText().trim();
                    String desc  = descF.getText().trim();
                    String venue = venueF.getText().trim();
                    String dateStr = dateF.getText().trim();
                    int cap = Integer.parseInt(capF.getText().trim());
                    String extra = extraF.getText().trim();
                    String type  = (String) typeBox.getSelectedItem();
                    LocalDateTime dt = LocalDateTime.parse(dateStr, CampusEvent.FORMATTER);
                    CampusEvent created;
                    if ("ACADEMIC".equals(type)) {
                        created = eventService.createAcademicEvent(title, desc, venue, dt, cap,
                                currentUser.getId(), extra.isEmpty() ? "General" : extra, false, 0);
                    } else if ("CULTURAL".equals(type)) {
                        created = eventService.createCulturalEvent(title, desc, venue, dt, cap,
                                currentUser.getId(), extra.isEmpty() ? "General" : extra, false);
                    } else {
                        created = eventService.createSportsEvent(title, desc, venue, dt, cap,
                                currentUser.getId(), extra.isEmpty() ? "General" : extra, false, 1);
                    }
                    JOptionPane.showMessageDialog(OrganizerDashboard.this,
                            "Event created! ID: " + created.getEventId(), "Success", JOptionPane.INFORMATION_MESSAGE);
                    titleF.setText(""); descF.setText(""); venueF.setText("");
                    dateF.setText(""); capF.setText(""); extraF.setText("");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(OrganizerDashboard.this,
                            "Capacity must be a number.", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(OrganizerDashboard.this,
                            ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        addRow(form, "Event Type",         typeBox);
        addRow(form, "Title",              titleF);
        addRow(form, "Description",        descF);
        addRow(form, "Venue",              venueF);
        addRow(form, "Date & Time",        dateF);
        addRow(form, "Max Capacity",       capF);
        addRow(form, "Dept / Theme / Sport", extraF);
        form.add(javax.swing.Box.createVerticalStrut(16));
        form.add(createBtn);

        outer.add(UIHelper.scrollPane(form), BorderLayout.CENTER);
        return outer;
    }

    private void addRow(JPanel p, String labelText, java.awt.Component field) {
        JLabel l = UIHelper.label(labelText, UIHelper.FONT_SMALL, UIHelper.TEXT_SECONDARY);
        l.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        p.add(l);
        p.add(javax.swing.Box.createVerticalStrut(4));
        p.add(field);
        p.add(javax.swing.Box.createVerticalStrut(12));
    }

    // ── My Events ──────────────────────────────────────────────────────────
    private JPanel buildMyEventsPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(UIHelper.BG_PAGE);
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        p.add(UIHelper.sectionTitle("📁 My Events"), BorderLayout.NORTH);

        JPanel list = new JPanel();
        list.setBackground(UIHelper.BG_PAGE);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        try {
            List<CampusEvent> events = eventService.getEventsByOrganizer(currentUser.getId());
            if (events.isEmpty()) {
                list.add(UIHelper.body("You have not created any events yet."));
            } else {
                for (CampusEvent ev : events) {
                    list.add(buildMyEventCard(ev));
                    list.add(javax.swing.Box.createVerticalStrut(10));
                }
            }
        } catch (Exception ex) {
            list.add(UIHelper.body("Error: " + ex.getMessage()));
        }
        p.add(UIHelper.scrollPane(list), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildMyEventCard(CampusEvent ev) {
        JPanel card = UIHelper.cardPanel();
        card.setLayout(new BorderLayout(10, 6));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titleRow.setOpaque(false);
        titleRow.add(UIHelper.h2(ev.getTitle()));
        titleRow.add(UIHelper.badge(ev.getEventType(), UIHelper.typeColor(ev.getEventType())));
        titleRow.add(UIHelper.badge(ev.getStatus(),    UIHelper.statusColor(ev.getStatus())));
        left.add(titleRow);
        left.add(UIHelper.body("📍 " + ev.getVenue() + "   🕐 " + ev.getDateTime().format(CampusEvent.FORMATTER)));
        left.add(UIHelper.body("Registrations: " + ev.getCurrentRegistrations() + " / " + ev.getMaxCapacity()));
        card.add(left, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        btns.setOpaque(false);

        JButton attendeesBtn = UIHelper.primaryButton("Attendees", UIHelper.ACCENT_BLUE);
        attendeesBtn.setPreferredSize(new Dimension(110, 32));
        attendeesBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { showPanel(buildAttendeesPanel(ev)); }
        });

        String[] statuses = {"UPCOMING", "ONGOING", "COMPLETED", "CANCELLED"};
        JComboBox<String> statusBox = new JComboBox<String>(statuses);
        statusBox.setSelectedItem(ev.getStatus());
        statusBox.setBackground(java.awt.Color.WHITE);
        statusBox.setForeground(UIHelper.TEXT_PRIMARY);
        statusBox.setFont(UIHelper.FONT_SMALL);
        statusBox.setPreferredSize(new Dimension(120, 32));
        statusBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String newStatus = (String) statusBox.getSelectedItem();
                try {
                    eventService.updateEventStatus(ev.getEventId(), newStatus);
                    showPanel(buildMyEventsPanel());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(OrganizerDashboard.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btns.add(attendeesBtn);
        btns.add(statusBox);
        card.add(btns, BorderLayout.EAST);
        return card;
    }

    // ── Attendees (Attendance Tracking) ────────────────────────────────────
    private JPanel buildAttendeesPanel(CampusEvent ev) {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(UIHelper.BG_PAGE);
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(UIHelper.sectionTitle("👥 Attendees — " + ev.getTitle()), BorderLayout.WEST);
        JButton backBtn = UIHelper.primaryButton("← Back", UIHelper.TEXT_SECONDARY);
        backBtn.setPreferredSize(new Dimension(90, 30));
        backBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { showPanel(buildMyEventsPanel()); }
        });
        header.add(backBtn, BorderLayout.EAST);
        p.add(header, BorderLayout.NORTH);

        JPanel list = new JPanel();
        list.setBackground(UIHelper.BG_PAGE);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        try {
            List<Registration> regs = eventService.getEventRegistrations(ev.getEventId());
            if (regs.isEmpty()) {
                list.add(UIHelper.body("No attendees registered yet."));
            } else {
                // header row
                JPanel headerRow = new JPanel(new GridLayout(1, 4, 10, 0));
                headerRow.setOpaque(false);
                headerRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
                headerRow.add(UIHelper.label("Reg ID",     UIHelper.FONT_SMALL, UIHelper.ACCENT_BLUE));
                headerRow.add(UIHelper.label("Student ID", UIHelper.FONT_SMALL, UIHelper.ACCENT_BLUE));
                headerRow.add(UIHelper.label("Date",       UIHelper.FONT_SMALL, UIHelper.ACCENT_BLUE));
                headerRow.add(UIHelper.label("Status",     UIHelper.FONT_SMALL, UIHelper.ACCENT_BLUE));
                list.add(headerRow);
                list.add(javax.swing.Box.createVerticalStrut(6));

                for (Registration r : regs) {
                    JPanel row = new JPanel(new GridLayout(1, 4, 10, 0));
                    row.setOpaque(false);
                    row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
                    row.add(UIHelper.label(r.getRegistrationId(), UIHelper.FONT_BODY, UIHelper.TEXT_PRIMARY));
                    row.add(UIHelper.label(r.getStudentId(),      UIHelper.FONT_BODY, UIHelper.TEXT_PRIMARY));
                    row.add(UIHelper.label(r.getRegisteredAt().format(CampusEvent.FORMATTER), UIHelper.FONT_BODY, UIHelper.TEXT_SECONDARY));
                    row.add(UIHelper.badge(r.getStatus(), UIHelper.statusColor(r.getStatus())));
                    list.add(row);
                    list.add(javax.swing.Box.createVerticalStrut(4));
                }
                list.add(javax.swing.Box.createVerticalStrut(10));
                list.add(UIHelper.label("Total Attendees: " + regs.size(), UIHelper.FONT_BODY, UIHelper.ACCENT_GREEN));
            }
        } catch (Exception ex) {
            list.add(UIHelper.body("Error loading attendees: " + ex.getMessage()));
        }
        p.add(UIHelper.scrollPane(list), BorderLayout.CENTER);
        return p;
    }

    // ── Event Tickets ──────────────────────────────────────────────────────
    private JPanel buildEventTicketsPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(UIHelper.BG_PAGE);
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        p.add(UIHelper.sectionTitle("Event Tickets & Payments"), BorderLayout.NORTH);

        JPanel list = new JPanel();
        list.setBackground(UIHelper.BG_PAGE);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        try {
            java.util.List<CampusEvent> myEvents = eventService.getEventsByOrganizer(currentUser.getId());
            if (myEvents.isEmpty()) {
                list.add(UIHelper.body("You have no events yet."));
            } else {
                for (CampusEvent ev : myEvents) {
                    java.util.List<Ticket> evTickets = ticketService.getTicketsByEvent(ev.getEventId());
                    JPanel card = UIHelper.cardPanel();
                    card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
                    card.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 120));

                    JPanel titleRow = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 0));
                    titleRow.setOpaque(false);
                    titleRow.add(UIHelper.h2(ev.getTitle()));
                    titleRow.add(UIHelper.badge(ev.getEventType(), UIHelper.typeColor(ev.getEventType())));
                    titleRow.add(UIHelper.label("  " + evTickets.size() + " tickets sold", UIHelper.FONT_SMALL, UIHelper.ACCENT_GREEN));
                    double rev = 0;
                    for (Ticket t : evTickets) if (!Ticket.STATUS_CANCELLED.equals(t.getStatus())) rev += t.getAmount();
                    titleRow.add(UIHelper.label("  Revenue: Tk " + String.format("%.0f", rev), UIHelper.FONT_SMALL, UIHelper.ACCENT_PURPLE));
                    card.add(titleRow);

                    for (Ticket t : evTickets) {
                        JPanel trow = new JPanel(new java.awt.GridLayout(1, 5, 8, 0));
                        trow.setOpaque(false);
                        trow.add(UIHelper.label(t.getTicketId(),     UIHelper.FONT_SMALL, UIHelper.TEXT_SECONDARY));
                        trow.add(UIHelper.label(t.getStudentId(),    UIHelper.FONT_SMALL, UIHelper.TEXT_PRIMARY));
                        trow.add(UIHelper.label(t.getTransactionId(),UIHelper.FONT_SMALL, UIHelper.ACCENT_BLUE));
                        trow.add(UIHelper.label("Tk " + String.format("%.0f", t.getAmount()), UIHelper.FONT_SMALL, UIHelper.ACCENT_GREEN));
                        Color sc = Ticket.STATUS_CONFIRMED.equals(t.getStatus()) ? UIHelper.ACCENT_GREEN
                                 : Ticket.STATUS_USED.equals(t.getStatus())      ? UIHelper.ACCENT_BLUE
                                 : UIHelper.ACCENT_RED;
                        trow.add(UIHelper.badge(t.getStatus(), sc));
                        card.add(trow);
                    }
                    list.add(card);
                    list.add(javax.swing.Box.createVerticalStrut(10));
                }
            }
        } catch (Exception ex) {
            list.add(UIHelper.body("Error: " + ex.getMessage()));
        }
        p.add(UIHelper.scrollPane(list), BorderLayout.CENTER);
        return p;
    }
}

