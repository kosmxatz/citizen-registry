package com.example.exception;

/**
 * Exception thrown when a citizen is not found
 */
public class CitizenNotFoundException extends RuntimeException {
    
    public CitizenNotFoundException(String message) {
        super(message);
    }
    
    public CitizenNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
