package com.smartcampus.service;

import com.smartcampus.exception.SmartCampusException;
import com.smartcampus.model.Registration;
import com.smartcampus.util.FileHandler;

import java.util.ArrayList;
import java.util.List;

public class RegistrationService {

    public List<Registration> getAllRegistrations() throws SmartCampusException {
        return FileHandler.loadAllRegistrations();
    }

    public List<Registration> getByStudent(String studentId) throws SmartCampusException {
        List<Registration> result = new ArrayList<Registration>();
        for (Registration r : FileHandler.loadAllRegistrations()) {
            if (r.getStudentId().equals(studentId)) result.add(r);
        }
        return result;
    }

    public List<Registration> getByEvent(String eventId) throws SmartCampusException {
        List<Registration> result = new ArrayList<Registration>();
        for (Registration r : FileHandler.loadAllRegistrations()) {
            if (r.getEventId().equals(eventId)) result.add(r);
        }
        return result;
    }
}
