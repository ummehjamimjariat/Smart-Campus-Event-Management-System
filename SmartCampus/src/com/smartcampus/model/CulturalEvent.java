package com.smartcampus.model;

import java.time.LocalDateTime;

public class CulturalEvent extends CampusEvent {

    private String theme;
    private boolean hasPerformance;

    public CulturalEvent(String eventId, String title, String description, String venue,
                         LocalDateTime dateTime, int maxCapacity, String organizerId,
                         String theme, boolean hasPerformance) {
        super(eventId, title, description, venue, dateTime, maxCapacity, organizerId);
        this.theme = theme;
        this.hasPerformance = hasPerformance;
    }

    public String getEventType() { return "CULTURAL"; }
    public String getEventIcon() { return "CULTURAL"; }
    public String getTheme() { return theme; }
    public boolean isHasPerformance() { return hasPerformance; }

    public String toCSV() {
        return super.toCSV() + "," + theme + "," + hasPerformance + ",0";
    }
}
