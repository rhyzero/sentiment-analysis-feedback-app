package com.example.sentimentanalysis.service;

import com.example.sentimentanalysis.dto.FeedbackDTO;
import com.example.sentimentanalysis.model.Feedback;
import com.example.sentimentanalysis.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Service class for handling feedback-related business logic.
 */
@Service
public class FeedbackService {

    private static final Logger logger = Logger.getLogger(FeedbackService.class.getName());

    private final FeedbackRepository feedbackRepository;
    private final SentimentAnalysisService sentimentAnalysisService;

    @Autowired
    public FeedbackService(FeedbackRepository feedbackRepository, 
                          SentimentAnalysisService sentimentAnalysisService) {
        this.feedbackRepository = feedbackRepository;
        this.sentimentAnalysisService = sentimentAnalysisService;
    }

    /**
     * Processes new feedback by analyzing sentiment and saving to the database.
     * 
     * @param feedbackDTO The feedback data from the client
     * @return The saved Feedback entity with sentiment analysis results
     */
    public Feedback processFeedback(FeedbackDTO feedbackDTO) {
        logger.info("Processing feedback: " + feedbackDTO.getText());
        
        // Create new feedback entity
        Feedback feedback = new Feedback();
        feedback.setText(feedbackDTO.getText());
        
        try {
            // Analyze sentiment
            Map<String, Object> sentimentResult = sentimentAnalysisService.analyzeSentiment(feedbackDTO.getText());
            logger.info("Sentiment analysis result: " + sentimentResult);
            
            // Set sentiment data with proper type checking
            if (sentimentResult != null) {
                if (sentimentResult.containsKey("label")) {
                    Object labelObj = sentimentResult.get("label");
                    if (labelObj != null) {
                        feedback.setSentimentLabel(labelObj.toString());
                    } else {
                        logger.warning("Sentiment label is null");
                    }
                } else {
                    logger.warning("Sentiment result does not contain 'label' key");
                }
                
                if (sentimentResult.containsKey("score")) {
                    Object scoreObj = sentimentResult.get("score");
                    if (scoreObj != null) {
                        if (scoreObj instanceof Double) {
                            feedback.setSentimentScore((Double) scoreObj);
                        } else if (scoreObj instanceof Number) {
                            feedback.setSentimentScore(((Number) scoreObj).doubleValue());
                        } else {
                            try {
                                feedback.setSentimentScore(Double.parseDouble(scoreObj.toString()));
                            } catch (NumberFormatException e) {
                                logger.warning("Could not parse sentiment score: " + scoreObj);
                            }
                        }
                    } else {
                        logger.warning("Sentiment score is null");
                    }
                } else {
                    logger.warning("Sentiment result does not contain 'score' key");
                }
            } else {
                logger.warning("Sentiment analysis returned null result");
            }
        } catch (Exception e) {
            logger.severe("Error during sentiment analysis: " + e.getMessage());
            // Continue anyway, saving the feedback without sentiment data
        }
        
        // Save and return
        Feedback savedFeedback = feedbackRepository.save(feedback);
        logger.info("Saved feedback with ID: " + savedFeedback.getId());
        return savedFeedback;
    }

    /**
     * Retrieves all feedback entries, ordered by creation date (newest first).
     * 
     * @return List of all Feedback entities
     */
    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * Retrieves a specific feedback entry by ID.
     * 
     * @param id The ID of the feedback to retrieve
     * @return Optional containing the Feedback if found, empty Optional otherwise
     */
    public Optional<Feedback> getFeedbackById(Long id) {
        return feedbackRepository.findById(id);
    }

    /**
     * Calculates sentiment statistics.
     * 
     * @return Map containing counts of positive, negative, and neutral feedback
     *         and the total count
     */
    public Map<String, Object> getSentimentStats() {
        int positiveCount = feedbackRepository.countBySentimentLabel("positive");
        int negativeCount = feedbackRepository.countBySentimentLabel("negative");
        int neutralCount = feedbackRepository.countBySentimentLabel("neutral");
        long totalCount = feedbackRepository.count();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("positive", positiveCount);
        stats.put("negative", negativeCount);
        stats.put("neutral", neutralCount);
        stats.put("total", totalCount);
        
        // Calculate percentages if there's data
        if (totalCount > 0) {
            stats.put("positivePercentage", (double) positiveCount / totalCount * 100);
            stats.put("negativePercentage", (double) negativeCount / totalCount * 100);
            stats.put("neutralPercentage", (double) neutralCount / totalCount * 100);
        }
        
        return stats;
    }
}