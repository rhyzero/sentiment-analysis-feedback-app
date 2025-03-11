package com.example.sentimentanalysis.repository;

import com.example.sentimentanalysis.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    
    List<Feedback> findAllByOrderByCreatedAtDesc();
    
    int countBySentimentLabel(String label);
}