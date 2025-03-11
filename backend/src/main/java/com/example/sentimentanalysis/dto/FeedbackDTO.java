package com.example.sentimentanalysis.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FeedbackDTO {
    
    @NotBlank(message = "Feedback text cannot be empty")
    @Size(min = 3, max = 1000, message = "Feedback must be between 3 and 1000 characters")
    private String text;
}