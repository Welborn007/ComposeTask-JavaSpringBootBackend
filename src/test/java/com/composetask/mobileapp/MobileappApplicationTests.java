package com.composetask.mobileapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@SpringBootTest
@ActiveProfiles("test") // Optional: use test profile
class MobileappApplicationTests {

    @Autowired
    private DataSource dataSource;

    @Test
    void testDatabaseConnection() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            boolean isValid = connection.isValid(2); // 2 second timeout
            assert isValid : "Database connection failed!";
            System.out.println("✓ Database connection successful!");
        } catch (SQLException e) {
            System.err.println("✗ Database connection failed: " + e.getMessage());
            throw e;
        }
    }

    @Test
    void contextLoads() {
        // Basic test to verify Spring context loads
    }
}
