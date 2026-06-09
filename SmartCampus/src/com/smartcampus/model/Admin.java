package com.smartcampus.model;

public class Admin extends Person {

    private String department;
    private int accessLevel;

    public Admin(String id, String name, String email, String password, String phone,
                 String department, int accessLevel) {
        super(id, name, email, password, phone);
        this.department = department;
        this.accessLevel = accessLevel;
    }

    public String getRole() { return "ADMIN"; }
    public String getDashboardTitle() { return "Admin Control Panel"; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public int getAccessLevel() { return accessLevel; }
    public void setAccessLevel(int accessLevel) { this.accessLevel = accessLevel; }

    public String toCSV() {
        return super.toCSV() + "," + department + "," + accessLevel;
    }
}
