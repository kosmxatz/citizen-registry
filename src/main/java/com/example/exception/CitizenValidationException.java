package com.example.exception;

/**
 * Exception thrown when citizen data validation fails
 */
public class CitizenValidationException extends RuntimeException {
    
    public CitizenValidationException(String message) {
        super(message);
    }
    
    public CitizenValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}