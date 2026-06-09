package com.smartcampus.service;

import com.smartcampus.exception.SmartCampusException;
import com.smartcampus.model.Ticket;
import com.smartcampus.util.FileHandler;
import com.smartcampus.util.IDGenerator;

import java.util.ArrayList;
import java.util.List;

public class TicketService {

    public Ticket issueTicket(String studentId, String eventId, String eventTitle,
                              double amount, String paymentMethod,
                              String payerNumber, String reference)
            throws SmartCampusException {

        String ticketId     = IDGenerator.generateTicketId();
        String transactionId = IDGenerator.generateTransactionId(paymentMethod);

        Ticket ticket = new Ticket(ticketId, studentId, eventId, eventTitle,
                amount, paymentMethod, payerNumber, transactionId, reference);
        FileHandler.saveTicket(ticket);
        return ticket;
    }

    public List<Ticket> getTicketsByStudent(String studentId) throws SmartCampusException {
        List<Ticket> result = new ArrayList<Ticket>();
        for (Ticket t : FileHandler.loadAllTickets()) {
            if (t.getStudentId().equals(studentId)) result.add(t);
        }
        return result;
    }

    public List<Ticket> getTicketsByEvent(String eventId) throws SmartCampusException {
        List<Ticket> result = new ArrayList<Ticket>();
        for (Ticket t : FileHandler.loadAllTickets()) {
            if (t.getEventId().equals(eventId)) result.add(t);
        }
        return result;
    }

    public List<Ticket> getAllTickets() throws SmartCampusException {
        return FileHandler.loadAllTickets();
    }

    public boolean hasTicket(String studentId, String eventId) throws SmartCampusException {
        for (Ticket t : FileHandler.loadAllTickets()) {
            if (t.getStudentId().equals(studentId)
                    && t.getEventId().equals(eventId)
                    && !Ticket.STATUS_CANCELLED.equals(t.getStatus())) {
                return true;
            }
        }
        return false;
    }

    public void markAsUsed(String ticketId) throws SmartCampusException {
        List<Ticket> tickets = FileHandler.loadAllTickets();
        for (Ticket t : tickets) {
            if (t.getTicketId().equals(ticketId)) {
                t.setStatus(Ticket.STATUS_USED);
                break;
            }
        }
        FileHandler.updateTickets(tickets);
        FileHandler.log("Ticket marked as USED: " + ticketId);
    }

    public void cancelTicket(String ticketId) throws SmartCampusException {
        List<Ticket> tickets = FileHandler.loadAllTickets();
        for (Ticket t : tickets) {
            if (t.getTicketId().equals(ticketId)) {
                t.setStatus(Ticket.STATUS_CANCELLED);
                break;
            }
        }
        FileHandler.updateTickets(tickets);
        FileHandler.log("Ticket cancelled: " + ticketId);
    }
}
