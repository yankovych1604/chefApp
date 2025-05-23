package com.maksy.chefapp.exception;

public class EntityNotFoundException extends RuntimeException {
    private final String statusCode;

    public EntityNotFoundException(String statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public String getStatusCode() {
        return statusCode;
    }
}