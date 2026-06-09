package com.smartcampus.service;

import com.smartcampus.exception.AuthenticationException;
import com.smartcampus.exception.SmartCampusException;
import com.smartcampus.model.Organizer;
import com.smartcampus.model.Person;
import com.smartcampus.model.Student;
import com.smartcampus.util.FileHandler;
import com.smartcampus.util.IDGenerator;

import java.util.List;

public class AuthService {

    public Person login(String email, String password, String role)
            throws AuthenticationException, SmartCampusException {
        if (email == null || email.trim().isEmpty())
            throw new AuthenticationException("Email cannot be empty.");
        if (password == null || password.trim().isEmpty())
            throw new AuthenticationException("Password cannot be empty.");
        List<Person> users = FileHandler.loadAllUsers();
        for (Person p : users) {
            if (p.getEmail().equalsIgnoreCase(email.trim()) && p.getPassword().equals(password)) {
                if (!p.getRole().equals(role))
                    throw new AuthenticationException("Wrong role selected. This account is: " + p.getRole());
                FileHandler.log("Login: " + email + " [" + role + "]");
                return p;
            }
        }
        throw new AuthenticationException("Invalid email or password.");
    }

    public Student registerStudent(String name, String email, String password,
                                   String phone, String studentId, String dept, int year)
            throws SmartCampusException {
        validate(name, email, password);
        Student s = new Student(IDGenerator.generateUserId("STUDENT"),
                name, email, password, phone, studentId, dept, year);
        FileHandler.saveUser(s);
        return s;
    }

    public Organizer registerOrganizer(String name, String email, String password,
                                       String phone, String org)
            throws SmartCampusException {
        validate(name, email, password);
        Organizer o = new Organizer(IDGenerator.generateUserId("ORGANIZER"),
                name, email, password, phone, org);
        FileHandler.saveUser(o);
        return o;
    }

    private void validate(String name, String email, String password) throws SmartCampusException {
        if (name == null || name.trim().isEmpty())
            throw new SmartCampusException("Name cannot be empty.");
        if (email == null || !email.contains("@"))
            throw new SmartCampusException("Enter a valid email address.");
        if (password == null || password.length() < 6)
            throw new SmartCampusException("Password must be at least 6 characters.");
        if (FileHandler.emailExists(email))
            throw new SmartCampusException("An account with this email already exists.");
    }
}
