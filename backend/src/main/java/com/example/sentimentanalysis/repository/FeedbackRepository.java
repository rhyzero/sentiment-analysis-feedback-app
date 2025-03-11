package com.example.sentimentanalysis.repository;

import com.example.sentimentanalysis.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Feedback entities.
 * Extends JpaRepository to inherit common CRUD operations.
 */
@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    
    /**
     * Finds all feedback entries ordered by creation date in descending order.
     * This means the most recent feedback will be returned first.
     * @return List of Feedback entities ordered by createdAt in descending order
     */
    List<Feedback> findAllByOrderByCreatedAtDesc();
    
    /**
     * Counts the number of feedback entries with a specific sentiment label.
     * @param label The sentiment label to count ('positive', 'negative','neutral')
     * @return The count of feedback entries with the given sentiment label
     */
    int countBySentimentLabel(String label);
}