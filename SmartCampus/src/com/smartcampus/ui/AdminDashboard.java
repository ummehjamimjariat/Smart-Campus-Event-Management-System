package com.smartcampus.ui;

import com.smartcampus.model.Admin;
import com.smartcampus.model.CampusEvent;
import com.smartcampus.model.Person;
import com.smartcampus.model.Registration;
import com.smartcampus.model.Ticket;
import com.smartcampus.service.EventService;
import com.smartcampus.service.TicketService;
import com.smartcampus.util.FileHandler;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class AdminDashboard extends BaseDashboard {

    private final EventService  eventService  = new EventService();
    private final TicketService ticketService = new TicketService();

    public AdminDashboard(Person user) { super(user); }

    protected JButton[] buildSidebarButtons() {
        JButton homeBtn    = sidebarBtn("", "Dashboard");
        JButton usersBtn   = sidebarBtn("", "All Users");
        JButton eventsBtn  = sidebarBtn("", "All Events");
        JButton regsBtn    = sidebarBtn("", "Registrations");
        JButton ticketsBtn = sidebarBtn("", "All Tickets");
        JButton logsBtn    = sidebarBtn("", "Activity Logs");
        JButton profileBtn = sidebarBtn("", "Profile");

        homeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { loadDefaultPanel(); }
        });
        usersBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { showPanel(buildUsersPanel()); }
        });
        eventsBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { showPanel(buildEventsPanel()); }
        });
        regsBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { showPanel(buildRegistrationsPanel()); }
        });
        ticketsBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { showPanel(buildAllTicketsPanel()); }
        });
        logsBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { showPanel(buildLogsPanel()); }
        });
        profileBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { showPanel(buildProfilePanel()); }
        });
        return new JButton[]{homeBtn, usersBtn, eventsBtn, regsBtn, logsBtn, profileBtn};
    }

    public void loadDefaultPanel() { showPanel(buildHomePanel()); }

    // ── Home ───────────────────────────────────────────────────────────────
    private JPanel buildHomePanel() {
        JPanel p = new JPanel(new BorderLayout(0, 20));
        p.setBackground(UIHelper.BG_PAGE);
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        p.add(UIHelper.title("Admin Control Panel 🛡"), BorderLayout.NORTH);

        try {
            List<Person> users       = FileHandler.loadAllUsers();
            List<CampusEvent> events = eventService.getAllEvents();
            List<Registration> regs  = FileHandler.loadAllRegistrations();

            long students   = 0, organizers = 0;
            for (Person u : users) {
                if ("STUDENT".equals(u.getRole()))   students++;
                if ("ORGANIZER".equals(u.getRole())) organizers++;
            }
            long upcoming = 0;
            for (CampusEvent e : events) {
                if ("UPCOMING".equals(e.getStatus())) upcoming++;
            }
            long confirmed = 0;
            for (Registration r : regs) {
                if ("CONFIRMED".equals(r.getStatus())) confirmed++;
            }

            JPanel stats = new JPanel(new GridLayout(2, 3, 16, 16));
            stats.setOpaque(false);
            stats.add(UIHelper.statCard(String.valueOf(users.size()),  "Total Users",       UIHelper.ACCENT_BLUE));
            stats.add(UIHelper.statCard(String.valueOf(students),      "Students",          UIHelper.ACCENT_GREEN));
            stats.add(UIHelper.statCard(String.valueOf(organizers),    "Organizers",        UIHelper.ACCENT_PURPLE));
            stats.add(UIHelper.statCard(String.valueOf(events.size()), "Total Events",      UIHelper.ACCENT_ORANGE));
            stats.add(UIHelper.statCard(String.valueOf(upcoming),      "Upcoming Events",   UIHelper.ACCENT_BLUE));
            stats.add(UIHelper.statCard(String.valueOf(confirmed),     "Active Regs",       UIHelper.ACCENT_GREEN));
            p.add(stats, BorderLayout.CENTER);
        } catch (Exception ex) {
            p.add(UIHelper.body("Error: " + ex.getMessage()), BorderLayout.CENTER);
        }
        return p;
    }

    // ── All Users ──────────────────────────────────────────────────────────
    private JPanel buildUsersPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(UIHelper.BG_PAGE);
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        p.add(UIHelper.sectionTitle("👥 All Users"), BorderLayout.NORTH);

        JPanel list = new JPanel();
        list.setBackground(UIHelper.BG_PAGE);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        try {
            List<Person> users = FileHandler.loadAllUsers();
            for (Person u : users) {
                JPanel card = UIHelper.cardPanel();
                card.setLayout(new BorderLayout(10, 4));
                card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

                JPanel left = new JPanel();
                left.setOpaque(false);
                left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

                JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
                row1.setOpaque(false);
                row1.add(UIHelper.h2(u.getName()));
                row1.add(UIHelper.badge(u.getRole(), UIHelper.typeColor(u.getRole())));
                left.add(row1);
                left.add(UIHelper.body("📧 " + u.getEmail() + "   🆔 " + u.getId()));
                card.add(left, BorderLayout.CENTER);
                list.add(card);
                list.add(javax.swing.Box.createVerticalStrut(8));
            }
            if (users.isEmpty()) list.add(UIHelper.body("No users found."));
        } catch (Exception ex) {
            list.add(UIHelper.body("Error: " + ex.getMessage()));
        }
        p.add(UIHelper.scrollPane(list), BorderLayout.CENTER);
        return p;
    }

    // ── All Events ─────────────────────────────────────────────────────────
    private JPanel buildEventsPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(UIHelper.BG_PAGE);
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        p.add(UIHelper.sectionTitle("🗓 All Events"), BorderLayout.NORTH);

        JPanel list = new JPanel();
        list.setBackground(UIHelper.BG_PAGE);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        try {
            List<CampusEvent> events = eventService.getAllEvents();
            for (CampusEvent ev : events) {
                JPanel card = UIHelper.cardPanel();
                card.setLayout(new BorderLayout(10, 4));
                card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

                JPanel left = new JPanel();
                left.setOpaque(false);
                left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

                JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
                row1.setOpaque(false);
                row1.add(UIHelper.h2(ev.getTitle()));
                row1.add(UIHelper.badge(ev.getEventType(), UIHelper.typeColor(ev.getEventType())));
                row1.add(UIHelper.badge(ev.getStatus(),    UIHelper.statusColor(ev.getStatus())));
                left.add(row1);
                left.add(UIHelper.body("📍 " + ev.getVenue() + "   🕐 " + ev.getDateTime().format(CampusEvent.FORMATTER)));
                left.add(UIHelper.body("Organizer: " + ev.getOrganizerId() + "   Seats: " + ev.getCurrentRegistrations() + "/" + ev.getMaxCapacity()));
                card.add(left, BorderLayout.CENTER);

                JButton cancelBtn = UIHelper.primaryButton("Cancel Event", UIHelper.ACCENT_RED);
                cancelBtn.setPreferredSize(new Dimension(120, 32));
                cancelBtn.setEnabled(!"CANCELLED".equals(ev.getStatus()));
                cancelBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        int ok = JOptionPane.showConfirmDialog(AdminDashboard.this,
                                "Cancel event: " + ev.getTitle() + "?", "Confirm", JOptionPane.YES_NO_OPTION);
                        if (ok == JOptionPane.YES_OPTION) {
                            try {
                                eventService.cancelEvent(ev.getEventId());
                                showPanel(buildEventsPanel());
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(AdminDashboard.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                });
                card.add(cancelBtn, BorderLayout.EAST);
                list.add(card);
                list.add(javax.swing.Box.createVerticalStrut(8));
            }
            if (events.isEmpty()) list.add(UIHelper.body("No events found."));
        } catch (Exception ex) {
            list.add(UIHelper.body("Error: " + ex.getMessage()));
        }
        p.add(UIHelper.scrollPane(list), BorderLayout.CENTER);
        return p;
    }

    // ── Registrations ──────────────────────────────────────────────────────
    private JPanel buildRegistrationsPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(UIHelper.BG_PAGE);
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        p.add(UIHelper.sectionTitle("📋 All Registrations"), BorderLayout.NORTH);

        JPanel list = new JPanel();
        list.setBackground(UIHelper.BG_PAGE);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        try {
            List<Registration> regs = FileHandler.loadAllRegistrations();

            // Header row
            JPanel hdr = new JPanel(new GridLayout(1, 4, 10, 0));
            hdr.setOpaque(false);
            hdr.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
            hdr.add(UIHelper.label("Reg ID",     UIHelper.FONT_SMALL, UIHelper.ACCENT_BLUE));
            hdr.add(UIHelper.label("Student ID", UIHelper.FONT_SMALL, UIHelper.ACCENT_BLUE));
            hdr.add(UIHelper.label("Event ID",   UIHelper.FONT_SMALL, UIHelper.ACCENT_BLUE));
            hdr.add(UIHelper.label("Status",     UIHelper.FONT_SMALL, UIHelper.ACCENT_BLUE));
            list.add(hdr);
            list.add(javax.swing.Box.createVerticalStrut(8));

            if (regs.isEmpty()) {
                list.add(UIHelper.body("No registrations found."));
            } else {
                for (Registration r : regs) {
                    JPanel row = new JPanel(new GridLayout(1, 4, 10, 0));
                    row.setOpaque(false);
                    row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
                    row.add(UIHelper.label(r.getRegistrationId(), UIHelper.FONT_BODY, UIHelper.TEXT_PRIMARY));
                    row.add(UIHelper.label(r.getStudentId(),      UIHelper.FONT_BODY, UIHelper.TEXT_PRIMARY));
                    row.add(UIHelper.label(r.getEventId(),        UIHelper.FONT_BODY, UIHelper.TEXT_PRIMARY));
                    row.add(UIHelper.badge(r.getStatus(), UIHelper.statusColor(r.getStatus())));
                    list.add(row);
                    list.add(javax.swing.Box.createVerticalStrut(4));
                }
            }
        } catch (Exception ex) {
            list.add(UIHelper.body("Error: " + ex.getMessage()));
        }
        p.add(UIHelper.scrollPane(list), BorderLayout.CENTER);
        return p;
    }

    // ── Activity Logs ──────────────────────────────────────────────────────
    private JPanel buildLogsPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(UIHelper.BG_PAGE);
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        p.add(UIHelper.sectionTitle("📜 Activity Logs"), BorderLayout.NORTH);

        JTextArea logArea = new JTextArea();
        logArea.setFont(UIHelper.FONT_MONO);
        logArea.setForeground(UIHelper.TEXT_PRIMARY);
        logArea.setBackground(UIHelper.BG_PAGE);
        logArea.setEditable(false);
        logArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        List<String> logs = FileHandler.readLogs();
        StringBuilder sb = new StringBuilder();
        for (int i = logs.size() - 1; i >= 0; i--) {
            sb.append(logs.get(i)).append("\n");
        }
        logArea.setText(logs.isEmpty() ? "No activity logs yet." : sb.toString());
        p.add(UIHelper.scrollPane(logArea), BorderLayout.CENTER);
        return p;
    }

    // ── Profile ────────────────────────────────────────────────────────────
    protected JPanel buildProfilePanel() {
        JPanel p = super.buildProfilePanel();
        if (currentUser instanceof Admin) {
            Admin a = (Admin) currentUser;
            JPanel card = (JPanel) ((JPanel) p.getComponent(1));
            addProfileRow(card, "Department",   a.getDepartment());
            addProfileRow(card, "Access Level", String.valueOf(a.getAccessLevel()));
        }
        return p;
    }

    // ── All Tickets ────────────────────────────────────────────────────────
    private JPanel buildAllTicketsPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(UIHelper.BG_PAGE);
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        p.add(UIHelper.sectionTitle("All Tickets — Payment Records"), BorderLayout.NORTH);

        JPanel list = new JPanel();
        list.setBackground(UIHelper.BG_PAGE);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        try {
            List<Ticket> tickets = ticketService.getAllTickets();

            // summary stats
            double totalRevenue = 0;
            for (Ticket t : tickets) {
                if (!Ticket.STATUS_CANCELLED.equals(t.getStatus())) totalRevenue += t.getAmount();
            }
            JPanel stats = new JPanel(new java.awt.GridLayout(1, 3, 12, 0));
            stats.setOpaque(false);
            stats.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 90));
            stats.add(UIHelper.statCard(String.valueOf(tickets.size()), "Total Tickets", UIHelper.ACCENT_PURPLE));
            stats.add(UIHelper.statCard("Tk " + String.format("%.0f", totalRevenue), "Total Revenue", UIHelper.ACCENT_GREEN));
            long cancelled = 0;
            for (Ticket t : tickets) if (Ticket.STATUS_CANCELLED.equals(t.getStatus())) cancelled++;
            stats.add(UIHelper.statCard(String.valueOf(cancelled), "Cancelled", UIHelper.ACCENT_RED));
            list.add(stats);
            list.add(javax.swing.Box.createVerticalStrut(16));

            // header row
            JPanel hdr = new JPanel(new java.awt.GridLayout(1, 6, 8, 0));
            hdr.setOpaque(false);
            hdr.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 26));
            for (String h : new String[]{"Ticket ID", "Student ID", "Event", "Method", "Amount", "Status"}) {
                hdr.add(UIHelper.label(h, UIHelper.FONT_SMALL, UIHelper.ACCENT_BLUE));
            }
            list.add(hdr);
            list.add(javax.swing.Box.createVerticalStrut(6));

            if (tickets.isEmpty()) {
                list.add(UIHelper.body("No tickets issued yet."));
            } else {
                for (Ticket t : tickets) {
                    JPanel row = new JPanel(new java.awt.GridLayout(1, 6, 8, 0));
                    row.setOpaque(false);
                    row.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 30));
                    row.add(UIHelper.label(t.getTicketId(),      UIHelper.FONT_SMALL, UIHelper.TEXT_PRIMARY));
                    row.add(UIHelper.label(t.getStudentId(),     UIHelper.FONT_SMALL, UIHelper.TEXT_PRIMARY));
                    row.add(UIHelper.label(t.getEventTitle(),    UIHelper.FONT_SMALL, UIHelper.TEXT_PRIMARY));
                    Color mc = Ticket.PAY_BKASH.equals(t.getPaymentMethod())  ? new java.awt.Color(220,0,80)
                             : Ticket.PAY_ROCKET.equals(t.getPaymentMethod()) ? new java.awt.Color(140,60,200)
                             : new java.awt.Color(255,140,0);
                    row.add(UIHelper.badge(t.getPaymentMethod(), mc));
                    row.add(UIHelper.label("Tk " + String.format("%.0f", t.getAmount()), UIHelper.FONT_SMALL, UIHelper.ACCENT_GREEN));
                    Color sc = Ticket.STATUS_CONFIRMED.equals(t.getStatus()) ? UIHelper.ACCENT_GREEN
                             : Ticket.STATUS_USED.equals(t.getStatus())      ? UIHelper.ACCENT_BLUE
                             : UIHelper.ACCENT_RED;
                    row.add(UIHelper.badge(t.getStatus(), sc));
                    list.add(row);
                    list.add(javax.swing.Box.createVerticalStrut(4));
                }
            }
        } catch (Exception ex) {
            list.add(UIHelper.body("Error loading tickets: " + ex.getMessage()));
        }
        p.add(UIHelper.scrollPane(list), BorderLayout.CENTER);
        return p;
    }
}

