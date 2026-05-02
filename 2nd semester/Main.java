import java.util.*;

// ---------------- USER CLASS ----------------
class User {
    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
    public class Main {
    public static void main(String[] args) {
        System.out.println("Program is running!");
    }
}

    public String getUsername() {
        return username;
    }

    public boolean checkPassword(String pass) {
        return password.equals(pass);
    }
}

// ---------------- EVENT CLASS ----------------
class Event {
    private String eventName;
    private List<String> attendees;

    public Event(String eventName) {
        this.eventName = eventName;
        this.attendees = new ArrayList<>();
    }

    public String getEventName() {
        return eventName;
    }

    public void addAttendee(String username) {
        attendees.add(username);
    }

    public void showAttendees() {
        System.out.println("Attendees for " + eventName + ":");
        if (attendees.isEmpty()) {
            System.out.println("No attendees yet.");
        } else {
            for (String name : attendees) {
                System.out.println("- " + name);
            }
        }
    }
}

// ---------------- MAIN CLASS ----------------
public class Main {

    static List<User> users = new ArrayList<>();
    static List<Event> events = new ArrayList<>();
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        while (true) {
            System.out.println("\n--- Smart Event Management System ---");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");

            int choice = sc.nextInt();
            sc.nextLine(); // clear buffer

            switch (choice) {
                case 1:
                    register();
                    break;
                case 2:
                    login();
                    break;
                case 3:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    // ---------------- REGISTER ----------------
    static void register() {
        System.out.print("Enter username: ");
        String username = sc.nextLine();

        System.out.print("Enter password: ");
        String password = sc.nextLine();

        users.add(new User(username, password));
        System.out.println("Registration successful!");
    }

    // ---------------- LOGIN ----------------
    static void login() {
        System.out.print("Enter username: ");
        String username = sc.nextLine();

        System.out.print("Enter password: ");
        String password = sc.nextLine();

        for (User user : users) {
            if (user.getUsername().equals(username) && user.checkPassword(password)) {
                System.out.println("Login successful!");
                userMenu(username);
                return;
            }
        }
        System.out.println("Invalid credentials!");
    }

    // ---------------- USER MENU ----------------
    static void userMenu(String username) {
        while (true) {
            System.out.println("\n--- User Menu ---");
            System.out.println("1. Create Event");
            System.out.println("2. View Events");
            System.out.println("3. RSVP (Join Event)");
            System.out.println("4. Attendance Tracking");
            System.out.println("5. Logout");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    createEvent();
                    break;
                case 2:
                    viewEvents();
                    break;
                case 3:
                    rsvp(username);
                    break;
                case 4:
                    attendance();
                    break;
                case 5:
                    System.out.println("Logged out!");
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    // ---------------- CREATE EVENT ----------------
    static void createEvent() {
        System.out.print("Enter event name: ");
        String name = sc.nextLine();

        events.add(new Event(name));
        System.out.println("Event created successfully!");
    }

    // ---------------- VIEW EVENTS ----------------
    static void viewEvents() {
        if (events.isEmpty()) {
            System.out.println("No events available.");
            return;
        }

        System.out.println("Events:");
        for (int i = 0; i < events.size(); i++) {
            System.out.println(i + ". " + events.get(i).getEventName());
        }
    }

    // ---------------- RSVP ----------------
    static void rsvp(String username) {
        viewEvents();
        if (events.isEmpty()) return;

        System.out.print("Select event number: ");
        int index = sc.nextInt();

        if (index >= 0 && index < events.size()) {
            events.get(index).addAttendee(username);
            System.out.println("RSVP successful!");
        } else {
            System.out.println("Invalid event!");
        }
    }

    // ---------------- ATTENDANCE ----------------
    static void attendance() {
        viewEvents();
        if (events.isEmpty()) return;

        System.out.print("Select event number: ");
        int index = sc.nextInt();

        if (index >= 0 && index < events.size()) {
            events.get(index).showAttendees();
        } else {
            System.out.println("Invalid event!");
        }
    }
}