package com.smartcampus.model;

import java.time.LocalDateTime;

public class SportsEvent extends CampusEvent {

    private String sportType;
    private boolean hasTeamRegistration;
    private int teamSize;

    public SportsEvent(String eventId, String title, String description, String venue,
                       LocalDateTime dateTime, int maxCapacity, String organizerId,
                       String sportType, boolean hasTeamRegistration, int teamSize) {
        super(eventId, title, description, venue, dateTime, maxCapacity, organizerId);
        this.sportType = sportType;
        this.hasTeamRegistration = hasTeamRegistration;
        this.teamSize = teamSize;
    }

    public String getEventType() { return "SPORTS"; }
    public String getEventIcon() { return "SPORTS"; }
    public String getSportType() { return sportType; }
    public boolean isHasTeamRegistration() { return hasTeamRegistration; }
    public int getTeamSize() { return teamSize; }

    public String toCSV() {
        return super.toCSV() + "," + sportType + "," + hasTeamRegistration + "," + teamSize;
    }
}
