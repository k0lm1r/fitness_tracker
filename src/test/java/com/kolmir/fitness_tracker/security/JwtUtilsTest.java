package com.kolmir.fitness_tracker.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class JwtUtilsTest {

    private JwtUtils jwtUtils;
    private static final String SECRET = "aUxvdmVZdWxpYW5hUG9rYXRvdmljaFZlcnlNdWNoaUxvdmVZdWxpYW5hUG9rYXRvdmljaFZlcnlNdWNo";

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", SECRET);
        ReflectionTestUtils.setField(jwtUtils, "accessTokenExpiration", 60000L);
        ReflectionTestUtils.setField(jwtUtils, "refreshTokenExpiration", 120000L);
    }

    @Test
    void generateAndValidateAccessToken() {
        String token = jwtUtils.generateAccessToken("tester");

        assertTrue(jwtUtils.validateToken(token));
        assertEquals("tester", jwtUtils.getUsernameFromToken(token));
    }

    @Test
    void generateAndValidateRefreshToken() {
        String token = jwtUtils.generateRefreshToken("tester2");

        assertTrue(jwtUtils.validateToken(token));
        assertEquals("tester2", jwtUtils.getUsernameFromToken(token));
    }

    @Test
    void validateToken_WhenInvalid_ReturnsFalse() {
        assertFalse(jwtUtils.validateToken("invalid.token.value"));
    }
}
