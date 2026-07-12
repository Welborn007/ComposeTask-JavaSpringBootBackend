package com.composetask.mobileapp.config;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JwtUtilTest {

    @Test
    void accessTokenContainsRoleClaim() {
        JwtUtil jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "SECRET_KEY", "this_is_a_secret_key_for_tests_only_1234567890");
        ReflectionTestUtils.setField(jwtUtil, "ACCESS_TOKEN_EXPIRATION", 900000L);

        String token = jwtUtil.generateAccessToken("vendor@example.com", "VENDOR");

        assertEquals("vendor@example.com", jwtUtil.extractEmail(token));
        assertEquals("VENDOR", jwtUtil.extractRole(token));
    }
}
