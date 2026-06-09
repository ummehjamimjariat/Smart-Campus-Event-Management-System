package com.smartcampus.service;

import com.smartcampus.exception.DuplicateRegistrationException;
import com.smartcampus.exception.EventFullException;
import com.smartcampus.exception.InvalidEventException;
import com.smartcampus.exception.SmartCampusException;
import com.smartcampus.model.AcademicEvent;
import com.smartcampus.model.CampusEvent;
import com.smartcampus.model.CulturalEvent;
import com.smartcampus.model.Registration;
import com.smartcampus.model.SportsEvent;
import com.smartcampus.util.FileHandler;
import com.smartcampus.util.IDGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventService {

    public AcademicEvent createAcademicEvent(String title, String desc, String venue,
            LocalDateTime dt, int cap, String orgId, String dept, boolean cert, int credits)
            throws SmartCampusException {
        validateEvent(title, venue, dt, cap);
        AcademicEvent e = new AcademicEvent(IDGenerator.generateEventId("ACADEMIC"),
                title, desc, venue, dt, cap, orgId, dept, cert, credits);
        FileHandler.saveEvent(e);
        return e;
    }

    public CulturalEvent createCulturalEvent(String title, String desc, String venue,
            LocalDateTime dt, int cap, String orgId, String theme, boolean perf)
            throws SmartCampusException {
        validateEvent(title, venue, dt, cap);
        CulturalEvent e = new CulturalEvent(IDGenerator.generateEventId("CULTURAL"),
                title, desc, venue, dt, cap, orgId, theme, perf);
        FileHandler.saveEvent(e);
        return e;
    }

    public SportsEvent createSportsEvent(String title, String desc, String venue,
            LocalDateTime dt, int cap, String orgId, String sport, boolean team, int teamSize)
            throws SmartCampusException {
        validateEvent(title, venue, dt, cap);
        SportsEvent e = new SportsEvent(IDGenerator.generateEventId("SPORTS"),
                title, desc, venue, dt, cap, orgId, sport, team, teamSize);
        FileHandler.saveEvent(e);
        return e;
    }

    private void validateEvent(String title, String venue, LocalDateTime dt, int cap)
            throws InvalidEventException {
        if (title == null || title.trim().isEmpty())
            throw new InvalidEventException("Event title cannot be empty.");
        if (venue == null || venue.trim().isEmpty())
            throw new InvalidEventException("Venue cannot be empty.");
        if (dt == null || dt.isBefore(LocalDateTime.now()))
            throw new InvalidEventException("Event date must be in the future.");
        if (cap < 1)
            throw new InvalidEventException("Capacity must be at least 1.");
    }

    public List<CampusEvent> getAllEvents() throws SmartCampusException {
        return FileHandler.loadAllEvents();
    }

    public List<CampusEvent> getUpcomingEvents() throws SmartCampusException {
        List<CampusEvent> result = new ArrayList<CampusEvent>();
        for (CampusEvent e : FileHandler.loadAllEvents()) {
            if ("UPCOMING".equals(e.getStatus())) result.add(e);
        }
        return result;
    }

    public List<CampusEvent> getEventsByOrganizer(String orgId) throws SmartCampusException {
        List<CampusEvent> result = new ArrayList<CampusEvent>();
        for (CampusEvent e : FileHandler.loadAllEvents()) {
            if (e.getOrganizerId().equals(orgId)) result.add(e);
        }
        return result;
    }

    public List<CampusEvent> searchEvents(String keyword) throws SmartCampusException {
        List<CampusEvent> result = new ArrayList<CampusEvent>();
        String kw = keyword.toLowerCase();
        for (CampusEvent e : FileHandler.loadAllEvents()) {
            if (e.getTitle().toLowerCase().contains(kw)
                    || e.getVenue().toLowerCase().contains(kw)
                    || e.getEventType().toLowerCase().contains(kw)) {
                result.add(e);
            }
        }
        return result;
    }

    public List<CampusEvent> searchEvents(String keyword, String type) throws SmartCampusException {
        List<CampusEvent> result = new ArrayList<CampusEvent>();
        for (CampusEvent e : searchEvents(keyword)) {
            if (type == null || type.isEmpty() || e.getEventType().equalsIgnoreCase(type))
                result.add(e);
        }
        return result;
    }

    public CampusEvent getEventById(String eventId) throws SmartCampusException {
        for (CampusEvent e : FileHandler.loadAllEvents()) {
            if (e.getEventId().equals(eventId)) return e;
        }
        return null;
    }

    public void cancelEvent(String eventId) throws SmartCampusException {
        List<CampusEvent> events = FileHandler.loadAllEvents();
        boolean found = false;
        for (CampusEvent e : events) {
            if (e.getEventId().equals(eventId)) { e.setStatus("CANCELLED"); found = true; break; }
        }
        if (!found) throw new InvalidEventException("Event not found: " + eventId);
        FileHandler.updateEvents(events);
        FileHandler.log("Event cancelled: " + eventId);
    }

    public void updateEventStatus(String eventId, String status) throws SmartCampusException {
        List<CampusEvent> events = FileHandler.loadAllEvents();
        for (CampusEvent e : events) {
            if (e.getEventId().equals(eventId)) { e.setStatus(status); break; }
        }
        FileHandler.updateEvents(events);
    }

    public Registration registerStudent(String studentId, String eventId)
            throws SmartCampusException {
        List<Registration> regs = FileHandler.loadAllRegistrations();
        for (Registration r : regs) {
            if (r.getStudentId().equals(studentId) && r.getEventId().equals(eventId)
                    && "CONFIRMED".equals(r.getStatus())) {
                CampusEvent ev = getEventById(eventId);
                throw new DuplicateRegistrationException(ev != null ? ev.getTitle() : eventId);
            }
        }
        List<CampusEvent> events = FileHandler.loadAllEvents();
        CampusEvent target = null;
        for (CampusEvent e : events) {
            if (e.getEventId().equals(eventId)) { target = e; break; }
        }
        if (target == null) throw new InvalidEventException("Event not found: " + eventId);
        if (!target.isAvailable()) throw new EventFullException(target.getTitle());
        target.registerSeat();
        FileHandler.updateEvents(events);
        Registration reg = new Registration(IDGenerator.generateRegistrationId(), studentId, eventId);
        FileHandler.saveRegistration(reg);
        return reg;
    }

    public void cancelRegistration(String studentId, String eventId) throws SmartCampusException {
        List<Registration> regs = FileHandler.loadAllRegistrations();
        boolean found = false;
        for (Registration r : regs) {
            if (r.getStudentId().equals(studentId) && r.getEventId().equals(eventId)
                    && "CONFIRMED".equals(r.getStatus())) {
                r.setStatus("CANCELLED");
                found = true;
                break;
            }
        }
        if (!found) throw new SmartCampusException("No active registration found.");
        FileHandler.updateRegistrations(regs);
        List<CampusEvent> events = FileHandler.loadAllEvents();
        for (CampusEvent e : events) {
            if (e.getEventId().equals(eventId)) { e.cancelSeat(); break; }
        }
        FileHandler.updateEvents(events);
        FileHandler.log("Cancelled registration: " + studentId + " for " + eventId);
    }

    public List<Registration> getStudentRegistrations(String studentId) throws SmartCampusException {
        List<Registration> result = new ArrayList<Registration>();
        for (Registration r : FileHandler.loadAllRegistrations()) {
            if (r.getStudentId().equals(studentId) && "CONFIRMED".equals(r.getStatus()))
                result.add(r);
        }
        return result;
    }

    public List<Registration> getEventRegistrations(String eventId) throws SmartCampusException {
        List<Registration> result = new ArrayList<Registration>();
        for (Registration r : FileHandler.loadAllRegistrations()) {
            if (r.getEventId().equals(eventId)) result.add(r);
        }
        return result;
    }
}
