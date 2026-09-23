package com.example.LibraryManagementSystem.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;


class JwtServiceTest {

    private static final String TEST_SECRET =
        "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=";

    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(
            TEST_SECRET,
            3_600_000
        );

        userDetails =
            org.springframework.security.core.userdetails.User
                .withUsername("member@example.com")
                .password("encoded-password")
                .roles("MEMBER")
                .build();
    }

    @Test
    void generateToken_shouldContainUsername() {
        // Act
        String token = jwtService.generateToken(userDetails);

        // Assert
        assertEquals(
            "member@example.com",
            jwtService.extractUsername(token)
        );
    }

    @Test
    void isTokenValid_shouldReturnTrue_whenUsernameMatches() {
        // Arrange
        String token = jwtService.generateToken(userDetails);

        // Act
        boolean result = jwtService.isTokenValid(
            token,
            userDetails
        );

        // Assert
        assertTrue(result);
    }

    @Test
    void isTokenValid_shouldReturnFalse_whenUsernameDoesNotMatch() {
        // Arrange
        String token = jwtService.generateToken(userDetails);

        UserDetails differentUser =
            org.springframework.security.core.userdetails.User
                .withUsername("different@example.com")
                .password("encoded-password")
                .roles("MEMBER")
                .build();

        // Act
        boolean result = jwtService.isTokenValid(
            token,
            differentUser
        );

        // Assert
        assertFalse(result);
    }

    @Test
    void isTokenValid_shouldReturnFalse_whenTokenIsMalformed() {
        // Act
        boolean result = jwtService.isTokenValid(
            "this-is-not-a-jwt",
            userDetails
        );

        // Assert
        assertFalse(result);
    }

    @Test
    void isTokenValid_shouldReturnFalse_whenTokenIsExpired() {
        // Arrange
        JwtService expiredJwtService = new JwtService(
            TEST_SECRET,
            -1_000
        );

        String expiredToken =
            expiredJwtService.generateToken(userDetails);

        // Act
        boolean result = expiredJwtService.isTokenValid(
            expiredToken,
            userDetails
        );

        // Assert
        assertFalse(result);
    }
}