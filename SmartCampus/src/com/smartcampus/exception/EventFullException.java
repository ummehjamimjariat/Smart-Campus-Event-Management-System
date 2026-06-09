package com.smartcampus.exception;
public class EventFullException extends SmartCampusException {
    public EventFullException(String eventTitle) {
        super("Event '" + eventTitle + "' is fully booked.");
    }
}
