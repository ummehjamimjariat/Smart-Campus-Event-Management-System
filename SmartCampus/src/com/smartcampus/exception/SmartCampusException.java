package com.smartcampus.exception;

public class SmartCampusException extends Exception {
    public SmartCampusException(String message) { super(message); }
    public SmartCampusException(String message, Throwable cause) { super(message, cause); }
}
