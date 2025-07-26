package com.example.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Global exception mapper for custom exceptions
 */
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {
    
    @Override
    public Response toResponse(Exception exception) {
        
        if (exception instanceof CitizenNotFoundException) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("NOT_FOUND", exception.getMessage()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
        
        if (exception instanceof CitizenValidationException) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("VALIDATION_ERROR", exception.getMessage()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
        
        // Generic server error
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
    
    // Inner class for error response format
    public static class ErrorResponse {
        private String errorCode;
        private String message;
        private long timestamp;
        
        public ErrorResponse(String errorCode, String message) {
            this.errorCode = errorCode;
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }
        
        // Getters
        public String getErrorCode() { return errorCode; }
        public String getMessage() { return message; }
        public long getTimestamp() { return timestamp; }
        
        // Setters (for JSON serialization)
        public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
        public void setMessage(String message) { this.message = message; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
}