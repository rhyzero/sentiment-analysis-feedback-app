package com.example.sentimentanalysis.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientException;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application.
 * This is an updated version that includes handling for our custom exceptions.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles validation exceptions thrown when request data fails validation.
     * For example, when a required field is missing or a field doesn't meet length requirements.
     * 
     * @param ex The validation exception that was thrown
     * @return A ResponseEntity containing validation error details and HTTP 400 Bad Request status
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        
        // Extract all field errors and their messages
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        
        // Return a 400 Bad Request with the error details
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handles SentimentAnalysisException.
     * 
     * @param ex The custom sentiment analysis exception
     * @return ResponseEntity with the error message and HTTP 500 status
     */
    @ExceptionHandler(SentimentAnalysisException.class)
    public ResponseEntity<Object> handleSentimentAnalysisException(SentimentAnalysisException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getMessage());
        error.put("error", "Sentiment Analysis Error");
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    /**
     * Handles RestClientException for when the ML service is unavailable.
     * 
     * @param ex The exception thrown when REST requests fail
     * @return ResponseEntity with the error message and HTTP 503 status
     */
    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<Object> handleRestClientException(RestClientException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", "ML service unavailable: " + ex.getMessage());
        error.put("error", "External Service Error");
        return new ResponseEntity<>(error, HttpStatus.SERVICE_UNAVAILABLE);
    }
    
    /**
     * Fallback handler for any unhandled exceptions.
     * This provides a generic error response for unexpected errors.
     * 
     * @param ex The exception that was thrown
     * @return A ResponseEntity with the error message and HTTP 500 Internal Server Error status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getMessage());
        error.put("error", "Internal Server Error");
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}