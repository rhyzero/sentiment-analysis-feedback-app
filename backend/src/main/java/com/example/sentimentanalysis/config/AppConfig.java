package com.example.sentimentanalysis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * General application configuration class.
 * Contains bean definitions that can be used throughout the application.
 */
@Configuration
public class AppConfig {
    
    /**
     * Creates a RestTemplate bean.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}