package com.example.sentimentanalysis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Simple controller for testing database connectivity.
 */
@RestController
public class TestController {

    /**
     * JdbcTemplate for executing SQL queries.
     * Automatically injected by Spring's dependency injection.
     */
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Test endpoint that verifies database connectivity.
     * Makes a simple SQL query to check if the database connection works.
     * @return A success message if the connection works, an error message otherwise
     */
    @GetMapping("/api/test-db")
    public String testDatabase() {
        try {
            // Execute a simple SQL query that doesn't depend on any tables
            String result = jdbcTemplate.queryForObject(
                "SELECT 'Database Connection Successful' as test", 
                String.class
            );
            return result;
        } catch (Exception e) {
            return "Database connection failed: " + e.getMessage();
        }
    }
}