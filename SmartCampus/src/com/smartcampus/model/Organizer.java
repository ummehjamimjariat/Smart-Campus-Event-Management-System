package com.smartcampus.model;

public class Organizer extends Person {

    private String organization;

    public Organizer(String id, String name, String email, String password, String phone,
                     String organization) {
        super(id, name, email, password, phone);
        this.organization = organization;
    }

    public String getRole() { return "ORGANIZER"; }
    public String getDashboardTitle() { return "Event Organizer Dashboard"; }
    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }

    public String toCSV() {
        return super.toCSV() + "," + organization;
    }
}
