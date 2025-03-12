package com.example.sentimentanalysis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service for communicating with the Python ML service for sentiment analysis.
 * If the ML service is unavailable, it falls back to a simple rule-based analysis.
 */
@Service
public class SentimentAnalysisService {

    private static final Logger logger = Logger.getLogger(SentimentAnalysisService.class.getName());

    @Value("${ml.service.url:http://localhost:5000/analyze}")
    private String mlServiceUrl;

    private final RestTemplate restTemplate;

    @Autowired
    public SentimentAnalysisService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Analyzes the sentiment of the given text.
     * Tries to use the ML service first, falls back to simple analysis if that fails.
     * 
     * @param text The text to analyze
     * @return A map containing the sentiment label and score
     */
    public Map<String, Object> analyzeSentiment(String text) {
        try {
            return callMlService(text);
        } catch (Exception e) {
            logger.log(Level.WARNING, "ML service unavailable. Using fallback analysis.", e);
            return simpleSentimentAnalysis(text);
        }
    }

    /**
     * Calls the ML service to analyze the text.
     * 
     * @param text The text to analyze
     * @return The sentiment analysis result from the ML service
     * @throws RestClientException if there's an error communicating with the ML service
     */
    private Map<String, Object> callMlService(String text) throws RestClientException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("text", text);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                mlServiceUrl, 
                request, 
                Map.class
        );

        //noinspection unchecked
        return response.getBody();
    }

    /**
     * Simple rule-based sentiment analysis as a fallback.
     * This is used when the ML service is unavailable.
     * 
     * @param text The text to analyze
     * @return A map containing the sentiment label and score
     */
    private Map<String, Object> simpleSentimentAnalysis(String text) {
        String lowercaseText = text.toLowerCase();
        Map<String, Object> result = new HashMap<>();

        // Simple positive and negative word lists
        String[] positiveWords = {"good", "great", "excellent", "amazing", "love", "happy", "best", "like", "awesome"};
        String[] negativeWords = {"bad", "terrible", "awful", "horrible", "hate", "worst", "dislike", "poor", "disappointing"};

        int positiveCount = 0;
        int negativeCount = 0;

        // Count positive and negative words
        for (String word : positiveWords) {
            if (lowercaseText.contains(word)) {
                positiveCount++;
            }
        }

        for (String word : negativeWords) {
            if (lowercaseText.contains(word)) {
                negativeCount++;
            }
        }

        // Determine sentiment based on word counts
        String label;
        double score;

        if (positiveCount > negativeCount) {
            label = "positive";
            score = 0.5 + (0.5 * (double) positiveCount / (positiveCount + negativeCount));
        } else if (negativeCount > positiveCount) {
            label = "negative";
            score = 0.5 * (double) negativeCount / (positiveCount + negativeCount);
        } else {
            label = "neutral";
            score = 0.5;
        }

        result.put("label", label);
        result.put("score", score);

        return result;
    }
}