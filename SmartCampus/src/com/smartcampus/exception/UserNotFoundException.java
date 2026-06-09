package com.smartcampus.exception;
public class UserNotFoundException extends SmartCampusException {
    public UserNotFoundException(String id) { super("User not found: " + id); }
}
