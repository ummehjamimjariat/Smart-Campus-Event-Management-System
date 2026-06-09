package com.smartcampus.exception;
public class DuplicateRegistrationException extends SmartCampusException {
    public DuplicateRegistrationException(String eventTitle) {
        super("You are already registered for '" + eventTitle + "'.");
    }
}
