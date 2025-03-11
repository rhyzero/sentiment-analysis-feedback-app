package com.example.sentimentanalysis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/api/test-db")
    public String testDatabase() {
        try {
            String result = jdbcTemplate.queryForObject("SELECT 'Database Connection Successful' as test", String.class);
            return result;
        } catch (Exception e) {
            return "Database connection failed: " + e.getMessage();
        }
    }
}