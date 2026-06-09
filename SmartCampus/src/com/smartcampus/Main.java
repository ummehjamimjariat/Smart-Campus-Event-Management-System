package com.smartcampus;

import com.smartcampus.ui.LoginFrame;
import com.smartcampus.util.FileHandler;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Smart Campus Event Management System
 * =====================================
 * OOP Concepts Demonstrated:
 *  Login / Registration   - LoginFrame with Sign In and Register tabs
 *  Encapsulation          - private fields, getters & setters in all model classes
 *  Inheritance            - Person -> Admin/Student/Organizer, CampusEvent -> AcademicEvent/CulturalEvent/SportsEvent
 *  Polymorphism           - method overloading (createEvent, searchEvents, login) and overriding (toString, getRole)
 *  Abstraction            - abstract classes Person and CampusEvent with abstract methods
 *  Exception Handling     - custom hierarchy: SmartCampusException and subclasses
 *  File Handling          - CSV persistence via FileHandler (users, events, registrations, logs)
 *  GUI                    - dark-themed Swing dashboards for Admin, Organizer, Student
 */
public class Main {

    public static void main(String[] args) {
        // Initialise data directory and seed default users
        FileHandler.log("Application started.");

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ignored) {}
                new LoginFrame();
            }
        });
    }
}
