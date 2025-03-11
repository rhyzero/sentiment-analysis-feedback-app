package com.example.sentimentanalysis.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data Transfer Object for feedback submission.
 * Used to validate and transfer feedback data from the client to the server.
 * 
 */
@Data  // Lombok annotation to generate getters, setters, equals, hashCode, and toString
public class FeedbackDTO {
    
    /**
     * The feedback text content.
     * 
     * @NotBlank: Ensures the text is not null and contains at least one non-whitespace character
     * @Size: Ensures the text is between 3 and 1000 characters long
     */
    @NotBlank(message = "Feedback text cannot be empty")
    @Size(min = 3, max = 1000, message = "Feedback must be between 3 and 1000 characters")
    private String text;
}