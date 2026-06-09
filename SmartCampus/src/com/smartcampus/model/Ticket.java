package com.smartcampus.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Ticket {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // Ticket statuses
    public static final String STATUS_PENDING   = "PENDING";
    public static final String STATUS_CONFIRMED = "CONFIRMED";
    public static final String STATUS_CANCELLED = "CANCELLED";
    public static final String STATUS_USED      = "USED";

    // Payment methods
    public static final String PAY_BKASH  = "bKash";
    public static final String PAY_ROCKET = "Rocket";
    public static final String PAY_NAGAD  = "Nagad";

    private String ticketId;
    private String studentId;
    private String eventId;
    private String eventTitle;
    private double amount;
    private String paymentMethod;   // bKash / Rocket / Nagad
    private String payerNumber;     // bKash/Rocket number used
    private String transactionId;   // TrxID generated
    private String reference;       // reference note
    private LocalDateTime paidAt;
    private String status;          // PENDING / CONFIRMED / CANCELLED / USED

    // Constructor for new ticket
    public Ticket(String ticketId, String studentId, String eventId, String eventTitle,
                  double amount, String paymentMethod, String payerNumber,
                  String transactionId, String reference) {
        this.ticketId       = ticketId;
        this.studentId      = studentId;
        this.eventId        = eventId;
        this.eventTitle     = eventTitle;
        this.amount         = amount;
        this.paymentMethod  = paymentMethod;
        this.payerNumber    = payerNumber;
        this.transactionId  = transactionId;
        this.reference      = reference;
        this.paidAt         = LocalDateTime.now();
        this.status         = STATUS_CONFIRMED;
    }

    // Constructor for loading from CSV
    public Ticket(String ticketId, String studentId, String eventId, String eventTitle,
                  double amount, String paymentMethod, String payerNumber,
                  String transactionId, String reference, LocalDateTime paidAt, String status) {
        this.ticketId       = ticketId;
        this.studentId      = studentId;
        this.eventId        = eventId;
        this.eventTitle     = eventTitle;
        this.amount         = amount;
        this.paymentMethod  = paymentMethod;
        this.payerNumber    = payerNumber;
        this.transactionId  = transactionId;
        this.reference      = reference;
        this.paidAt         = paidAt;
        this.status         = status;
    }

    // Getters
    public String getTicketId()       { return ticketId; }
    public String getStudentId()      { return studentId; }
    public String getEventId()        { return eventId; }
    public String getEventTitle()     { return eventTitle; }
    public double getAmount()         { return amount; }
    public String getPaymentMethod()  { return paymentMethod; }
    public String getPayerNumber()    { return payerNumber; }
    public String getTransactionId()  { return transactionId; }
    public String getReference()      { return reference; }
    public LocalDateTime getPaidAt()  { return paidAt; }
    public String getStatus()         { return status; }
    public void setStatus(String s)   { this.status = s; }

    public String toCSV() {
        String[] dt = paidAt.format(FORMATTER).split(" ");
        return ticketId + "," + studentId + "," + eventId + ","
             + eventTitle.replace(",", ";") + "," + amount + ","
             + paymentMethod + "," + payerNumber + "," + transactionId + ","
             + reference.replace(",", ";") + "," + dt[0] + "," + dt[1] + "," + status;
    }

    public String toString() {
        return "Ticket[" + ticketId + "] " + eventTitle + " | " + paymentMethod
             + " | Tk " + amount + " | TrxID: " + transactionId + " | " + status;
    }
}
