package com.smartcampus.model;

import java.time.LocalDateTime;

public class AcademicEvent extends CampusEvent {

    private String department;
    private boolean certificateProvided;
    private int creditHours;

    public AcademicEvent(String eventId, String title, String description, String venue,
                         LocalDateTime dateTime, int maxCapacity, String organizerId,
                         String department, boolean certificateProvided, int creditHours) {
        super(eventId, title, description, venue, dateTime, maxCapacity, organizerId);
        this.department = department;
        this.certificateProvided = certificateProvided;
        this.creditHours = creditHours;
    }

    public String getEventType() { return "ACADEMIC"; }
    public String getEventIcon() { return "ACADEMIC"; }
    public String getDepartment() { return department; }
    public boolean isCertificateProvided() { return certificateProvided; }
    public int getCreditHours() { return creditHours; }

    public String toCSV() {
        return super.toCSV() + "," + department + "," + certificateProvided + "," + creditHours;
    }
}
