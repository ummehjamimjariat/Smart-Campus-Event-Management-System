package com.smartcampus.model;

public class Student extends Person {

    private String studentId;
    private String department;
    private int year;

    public Student(String id, String name, String email, String password, String phone,
                   String studentId, String department, int year) {
        super(id, name, email, password, phone);
        this.studentId = studentId;
        this.department = department;
        this.year = year;
    }

    public String getRole() { return "STUDENT"; }
    public String getDashboardTitle() { return "Student Event Portal"; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public String toCSV() {
        return super.toCSV() + "," + studentId + "," + department + "," + year;
    }
}
