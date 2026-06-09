package com.smartcampus.util;

import com.smartcampus.exception.SmartCampusException;
import com.smartcampus.model.AcademicEvent;
import com.smartcampus.model.Admin;
import com.smartcampus.model.CampusEvent;
import com.smartcampus.model.CulturalEvent;
import com.smartcampus.model.Organizer;
import com.smartcampus.model.Person;
import com.smartcampus.model.Registration;
import com.smartcampus.model.SportsEvent;
import com.smartcampus.model.Student;
import com.smartcampus.model.Ticket;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    private static final String DATA_DIR    = "data/";
    private static final String USERS_FILE  = DATA_DIR + "users.csv";
    private static final String EVENTS_FILE = DATA_DIR + "events.csv";
    private static final String REGS_FILE   = DATA_DIR + "registrations.csv";
    private static final String TICKET_FILE = DATA_DIR + "tickets.csv";
    private static final String LOG_FILE    = DATA_DIR + "activity.log";

    static {
        new File(DATA_DIR).mkdirs();
        initFiles();
    }

    private static void initFiles() {
        createIfMissing(USERS_FILE,  "id,name,email,password,phone,role,extra1,extra2,extra3");
        createIfMissing(EVENTS_FILE, "eventId,title,desc,venue,dateTime,maxCap,curReg,orgId,status,type,e1,e2,e3");
        createIfMissing(REGS_FILE,   "regId,studentId,eventId,date,time,status");
        createIfMissing(TICKET_FILE, "ticketId,studentId,eventId,eventTitle,amount,method,payerNumber,transactionId,reference,date,time,status");
        try {
            List<String> users = readLines(USERS_FILE);
            boolean hasAdmin = false;
            for (String l : users) { if (l.contains("ADMIN")) { hasAdmin = true; break; } }
            if (!hasAdmin) {
                appendLine(USERS_FILE, "ADM001,System Admin,admin@campus.edu,admin123,01700000000,ADMIN,IT,5");
                appendLine(USERS_FILE, "ORG001,Event Club,events@campus.edu,org123,01711111111,ORGANIZER,Campus Events Club");
            }
        } catch (Exception ignored) {}
    }

    private static void createIfMissing(String path, String header) {
        File f = new File(path);
        if (!f.exists()) {
            try { PrintWriter pw = new PrintWriter(new FileWriter(f)); pw.println(header); pw.close(); }
            catch (IOException e) { System.err.println("Cannot create: " + path); }
        }
    }

    public static List<String> readLines(String filePath) throws SmartCampusException {
        List<String> lines = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String line; boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; }
                if (!line.trim().isEmpty()) lines.add(line.trim());
            }
            br.close();
        } catch (FileNotFoundException e) {
            throw new SmartCampusException("File not found: " + filePath);
        } catch (IOException e) {
            throw new SmartCampusException("Error reading: " + filePath);
        }
        return lines;
    }

    public static void appendLine(String filePath, String line) throws SmartCampusException {
        try { PrintWriter pw = new PrintWriter(new FileWriter(filePath, true)); pw.println(line); pw.close(); }
        catch (IOException e) { throw new SmartCampusException("Error writing: " + filePath); }
    }

    public static void rewriteFile(String filePath, String header, List<String> lines) throws SmartCampusException {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(filePath, false));
            pw.println(header);
            for (String l : lines) pw.println(l);
            pw.close();
        } catch (IOException e) { throw new SmartCampusException("Error rewriting: " + filePath); }
    }

    // ── Users ───────────────────────────────────────────────────────────────
    public static void saveUser(Person person) throws SmartCampusException {
        appendLine(USERS_FILE, person.toCSV());
        log("User registered: " + person.getEmail() + " [" + person.getRole() + "]");
    }

    public static List<Person> loadAllUsers() throws SmartCampusException {
        List<Person> users = new ArrayList<Person>();
        for (String line : readLines(USERS_FILE)) {
            Person p = parseUser(line);
            if (p != null) users.add(p);
        }
        return users;
    }

    private static Person parseUser(String line) {
        try {
            String[] p = line.split(",", -1);
            String role = p[5];
            if ("ADMIN".equals(role))
                return new Admin(p[0], p[1], p[2], p[3], p[4],
                        p.length > 6 ? p[6] : "IT",
                        p.length > 7 ? Integer.parseInt(p[7].trim()) : 1);
            if ("STUDENT".equals(role))
                return new Student(p[0], p[1], p[2], p[3], p[4],
                        p.length > 6 ? p[6] : p[0],
                        p.length > 7 ? p[7] : "General",
                        p.length > 8 ? Integer.parseInt(p[8].trim()) : 1);
            if ("ORGANIZER".equals(role))
                return new Organizer(p[0], p[1], p[2], p[3], p[4],
                        p.length > 6 ? p[6] : "Campus");
        } catch (Exception ignored) {}
        return null;
    }

    public static boolean emailExists(String email) throws SmartCampusException {
        for (Person u : loadAllUsers()) {
            if (u.getEmail().equalsIgnoreCase(email)) return true;
        }
        return false;
    }

    // ── Events ──────────────────────────────────────────────────────────────
    public static void saveEvent(CampusEvent event) throws SmartCampusException {
        appendLine(EVENTS_FILE, event.toCSV());
        log("Event created: " + event.getTitle());
    }

    public static List<CampusEvent> loadAllEvents() throws SmartCampusException {
        List<CampusEvent> events = new ArrayList<CampusEvent>();
        for (String line : readLines(EVENTS_FILE)) {
            CampusEvent e = parseEvent(line);
            if (e != null) events.add(e);
        }
        return events;
    }

    private static CampusEvent parseEvent(String line) {
        try {
            String[] p = line.split(",", -1);
            String eventId = p[0], title = p[1], desc = p[2].replace(";", ","), venue = p[3];
            LocalDateTime dt = LocalDateTime.parse(p[4], CampusEvent.FORMATTER);
            int maxCap = Integer.parseInt(p[5].trim());
            int curReg = Integer.parseInt(p[6].trim());
            String orgId = p[7], status = p[8], type = p[9];
            CampusEvent ev = null;
            if ("ACADEMIC".equals(type))
                ev = new AcademicEvent(eventId, title, desc, venue, dt, maxCap, orgId,
                        p.length > 10 ? p[10] : "General",
                        p.length > 11 && Boolean.parseBoolean(p[11]),
                        p.length > 12 ? Integer.parseInt(p[12].trim()) : 0);
            else if ("CULTURAL".equals(type))
                ev = new CulturalEvent(eventId, title, desc, venue, dt, maxCap, orgId,
                        p.length > 10 ? p[10] : "General",
                        p.length > 11 && Boolean.parseBoolean(p[11]));
            else if ("SPORTS".equals(type))
                ev = new SportsEvent(eventId, title, desc, venue, dt, maxCap, orgId,
                        p.length > 10 ? p[10] : "General",
                        p.length > 11 && Boolean.parseBoolean(p[11]),
                        p.length > 12 ? Integer.parseInt(p[12].trim()) : 1);
            if (ev != null) {
                ev.setStatus(status);
                for (int i = 0; i < curReg; i++) ev.registerSeat();
            }
            return ev;
        } catch (Exception ignored) {}
        return null;
    }

    public static void updateEvents(List<CampusEvent> events) throws SmartCampusException {
        List<String> lines = new ArrayList<String>();
        for (CampusEvent e : events) lines.add(e.toCSV());
        rewriteFile(EVENTS_FILE,
                "eventId,title,desc,venue,dateTime,maxCap,curReg,orgId,status,type,e1,e2,e3", lines);
    }

    // ── Registrations ────────────────────────────────────────────────────────
    public static void saveRegistration(Registration reg) throws SmartCampusException {
        appendLine(REGS_FILE, reg.toCSV());
        log("Registered: " + reg.getStudentId() + " for " + reg.getEventId());
    }

    public static List<Registration> loadAllRegistrations() throws SmartCampusException {
        List<Registration> regs = new ArrayList<Registration>();
        for (String line : readLines(REGS_FILE)) {
            try {
                String[] p = line.split(",");
                if (p.length < 6) continue;
                LocalDateTime dt = LocalDateTime.parse(p[3].trim() + " " + p[4].trim(), Registration.FORMATTER);
                regs.add(new Registration(p[0].trim(), p[1].trim(), p[2].trim(), dt, p[5].trim()));
            } catch (Exception ignored) {}
        }
        return regs;
    }

    public static void updateRegistrations(List<Registration> regs) throws SmartCampusException {
        List<String> lines = new ArrayList<String>();
        for (Registration r : regs) lines.add(r.toCSV());
        rewriteFile(REGS_FILE, "regId,studentId,eventId,date,time,status", lines);
    }

    // ── Tickets ──────────────────────────────────────────────────────────────
    public static void saveTicket(Ticket ticket) throws SmartCampusException {
        appendLine(TICKET_FILE, ticket.toCSV());
        log("Ticket issued: " + ticket.getTicketId() + " | " + ticket.getPaymentMethod()
                + " | TrxID: " + ticket.getTransactionId() + " | Tk " + ticket.getAmount());
    }

    public static List<Ticket> loadAllTickets() throws SmartCampusException {
        List<Ticket> tickets = new ArrayList<Ticket>();
        for (String line : readLines(TICKET_FILE)) {
            Ticket t = parseTicket(line);
            if (t != null) tickets.add(t);
        }
        return tickets;
    }

    private static Ticket parseTicket(String line) {
        try {
            String[] p = line.split(",", -1);
            // ticketId,studentId,eventId,eventTitle,amount,method,payerNumber,transactionId,reference,date,time,status
            if (p.length < 12) return null;
            String ticketId   = p[0].trim();
            String studentId  = p[1].trim();
            String eventId    = p[2].trim();
            String eventTitle = p[3].replace(";", ",").trim();
            double amount     = Double.parseDouble(p[4].trim());
            String method     = p[5].trim();
            String payerNum   = p[6].trim();
            String trxId      = p[7].trim();
            String reference  = p[8].replace(";", ",").trim();
            LocalDateTime dt  = LocalDateTime.parse(p[9].trim() + " " + p[10].trim(), Ticket.FORMATTER);
            String status     = p[11].trim();
            return new Ticket(ticketId, studentId, eventId, eventTitle, amount,
                    method, payerNum, trxId, reference, dt, status);
        } catch (Exception ignored) {}
        return null;
    }

    public static void updateTickets(List<Ticket> tickets) throws SmartCampusException {
        List<String> lines = new ArrayList<String>();
        for (Ticket t : tickets) lines.add(t.toCSV());
        rewriteFile(TICKET_FILE,
                "ticketId,studentId,eventId,eventTitle,amount,method,payerNumber,transactionId,reference,date,time,status",
                lines);
    }

    // ── Logs ─────────────────────────────────────────────────────────────────
    public static void log(String msg) {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(LOG_FILE, true));
            pw.println("[" + LocalDateTime.now().format(CampusEvent.FORMATTER) + "] " + msg);
            pw.close();
        } catch (IOException ignored) {}
    }

    public static List<String> readLogs() {
        List<String> logs = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(LOG_FILE));
            String line;
            while ((line = br.readLine()) != null) logs.add(line);
            br.close();
        } catch (IOException ignored) {}
        return logs;
    }
}
