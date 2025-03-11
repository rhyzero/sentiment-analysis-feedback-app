package com.example.sentimentanalysis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Sentiment Analysis Spring Boot application.
 * The @SpringBootApplication annotation combines @Configuration, @EnableAutoConfiguration,
 * and @ComponentScan with their default attributes.
 */
@SpringBootApplication
public class SentimentAnalysisApplication {
    public static void main(String[] args) {
        SpringApplication.run(SentimentAnalysisApplication.class, args);
    }
}