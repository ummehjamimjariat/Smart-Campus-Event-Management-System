package com.smartcampus.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class CampusEvent {

    private String eventId;
    private String title;
    private String description;
    private String venue;
    private LocalDateTime dateTime;
    private int maxCapacity;
    private int currentRegistrations;
    private String organizerId;
    private String status;

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public CampusEvent(String eventId, String title, String description, String venue,
                 LocalDateTime dateTime, int maxCapacity, String organizerId) {
        this.eventId = eventId;
        this.title = title;
        this.description = description;
        this.venue = venue;
        this.dateTime = dateTime;
        this.maxCapacity = maxCapacity;
        this.currentRegistrations = 0;
        this.organizerId = organizerId;
        this.status = "UPCOMING";
    }

    public abstract String getEventType();
    public abstract String getEventIcon();

    public boolean isAvailable() {
        return currentRegistrations < maxCapacity && "UPCOMING".equals(status);
    }

    public boolean registerSeat() {
        if (!isAvailable()) return false;
        currentRegistrations++;
        return true;
    }

    public boolean cancelSeat() {
        if (currentRegistrations <= 0) return false;
        currentRegistrations--;
        return true;
    }

    public String getEventId() { return eventId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String d) { this.description = d; }
    public String getVenue() { return venue; }
    public void setVenue(String v) { this.venue = v; }
    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dt) { this.dateTime = dt; }
    public int getMaxCapacity() { return maxCapacity; }
    public void setMaxCapacity(int c) { this.maxCapacity = c; }
    public int getCurrentRegistrations() { return currentRegistrations; }
    public int getAvailableSeats() { return maxCapacity - currentRegistrations; }
    public String getOrganizerId() { return organizerId; }
    public String getStatus() { return status; }
    public void setStatus(String s) { this.status = s; }

    public String toCSV() {
        return eventId + "," + title + "," + description.replace(",", ";") + "," +
               venue + "," + dateTime.format(FORMATTER) + "," +
               maxCapacity + "," + currentRegistrations + "," +
               organizerId + "," + status + "," + getEventType();
    }

    public String toString() {
        return "[" + getEventType() + "] " + title + " | " + venue + " | " +
               dateTime.format(FORMATTER) + " | " + status + " | " +
               currentRegistrations + "/" + maxCapacity;
    }
}
