package com.example.sentimentanalysis.controller;

import com.example.sentimentanalysis.dto.FeedbackDTO;
import com.example.sentimentanalysis.model.Feedback;
import com.example.sentimentanalysis.service.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for handling feedback-related operations.
 */
@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @Autowired
    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    /**
     * Submits new feedback and performs sentiment analysis.
     * 
     * @param feedbackDTO The feedback data from the request body
     * @return ResponseEntity with the created Feedback entity
     */
    @PostMapping
    public ResponseEntity<Feedback> submitFeedback(@Valid @RequestBody FeedbackDTO feedbackDTO) {
        Feedback savedFeedback = feedbackService.processFeedback(feedbackDTO);
        return new ResponseEntity<>(savedFeedback, HttpStatus.CREATED);
    }

    /**
     * Retrieves all feedback entries ordered by creation date (newest first).
     * 
     * @return List of all Feedback entities
     */
    @GetMapping
    public ResponseEntity<List<Feedback>> getAllFeedback() {
        List<Feedback> feedbackList = feedbackService.getAllFeedback();
        return ResponseEntity.ok(feedbackList);
    }

    /**
     * Retrieves a specific feedback entry by ID.
     * 
     * @param id The ID of the feedback to retrieve
     * @return ResponseEntity with the found Feedback entity or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Feedback> getFeedbackById(@PathVariable Long id) {
        return feedbackService.getFeedbackById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Retrieves sentiment statistics.
     * 
     * @return Map containing counts of positive, negative, and neutral feedback
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getSentimentStats() {
        Map<String, Object> stats = feedbackService.getSentimentStats();
        return ResponseEntity.ok(stats);
    }
}