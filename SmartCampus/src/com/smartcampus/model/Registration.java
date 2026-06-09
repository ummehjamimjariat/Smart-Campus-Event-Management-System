package com.smartcampus.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Registration {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private String registrationId;
    private String studentId;
    private String eventId;
    private LocalDateTime registeredAt;
    private String status;

    public Registration(String registrationId, String studentId, String eventId) {
        this.registrationId = registrationId;
        this.studentId = studentId;
        this.eventId = eventId;
        this.registeredAt = LocalDateTime.now();
        this.status = "CONFIRMED";
    }

    public Registration(String registrationId, String studentId, String eventId,
                        LocalDateTime registeredAt, String status) {
        this.registrationId = registrationId;
        this.studentId = studentId;
        this.eventId = eventId;
        this.registeredAt = registeredAt;
        this.status = status;
    }

    public String getRegistrationId() { return registrationId; }
    public String getStudentId() { return studentId; }
    public String getEventId() { return eventId; }
    public LocalDateTime getRegisteredAt() { return registeredAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String toCSV() {
        // Store date and time as separate columns so CSV split works correctly
        String[] parts = registeredAt.format(FORMATTER).split(" ");
        return registrationId + "," + studentId + "," + eventId + "," +
               parts[0] + "," + parts[1] + "," + status;
    }

    public String toString() {
        return "RegID: " + registrationId + " | Student: " + studentId +
               " | Event: " + eventId + " | " + status;
    }
}
