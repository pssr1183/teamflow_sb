package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DatabaseTestController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/test-connection")
    public String testConnection() {
        try {
            // Query to test database connectivity
            String sql = "INSERT into employee (name,position,salary,hire_date) values ('sai', 'ase', 470000, '2024-11-10')";
            int result = jdbcTemplate.update(sql);
            return "Database connection successful: ";
        } catch (Exception e) {
            return "Error connecting to the database: " + e.getMessage();
        }
    }
}
