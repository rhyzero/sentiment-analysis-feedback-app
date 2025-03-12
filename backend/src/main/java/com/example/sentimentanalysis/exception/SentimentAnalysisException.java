package com.example.sentimentanalysis.exception;

/**
 * Custom exception for sentiment analysis related errors.
 */
public class SentimentAnalysisException extends RuntimeException {

    /**
     * Creates a new exception with the specified message.
     * 
     * @param message The error message
     */
    public SentimentAnalysisException(String message) {
        super(message);
    }

    /**
     * Creates a new exception with the specified message and cause.
     * 
     * @param message The error message
     * @param cause The cause of the exception
     */
    public SentimentAnalysisException(String message, Throwable cause) {
        super(message, cause);
    }
}