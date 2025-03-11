package com.example.sentimentanalysis.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class for web-related settings.
 * This particular class configures Cross-Origin Resource Sharing (CORS).
 * CORS is a security feature implemented by browsers that restricts web page requests
 * to another domain outside the domain from which the first resource was served.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")                  // Apply CORS to all API endpoints
                .allowedOrigins("http://localhost:5173") // Allow requests from the React dev server
                .allowedMethods("GET", "POST", "PUT", "DELETE") // Allow these HTTP methods
                .allowedHeaders("*");                   // Allow all headers
    }
}