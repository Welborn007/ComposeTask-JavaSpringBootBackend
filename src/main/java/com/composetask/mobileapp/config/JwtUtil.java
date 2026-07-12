package com.composetask.mobileapp.config;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret:this_is_a_secret_key_for_demo_purpose_please_change_it}")
    private String SECRET_KEY;

    @Value("${jwt.expiration:900000}")  // 15 minutes in ms
    private long ACCESS_TOKEN_EXPIRATION;

    @Value("${jwt.refresh.expiration:604800000}")  // 7 days in ms
    private long REFRESH_TOKEN_EXPIRATION;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateAccessToken(String email) {
        return generateToken(email, ACCESS_TOKEN_EXPIRATION);
    }

    public String generateAccessToken(String email, String role) {
        return generateToken(email, role, ACCESS_TOKEN_EXPIRATION);
    }

    public String generateRefreshToken(String email) {
        return generateToken(email, REFRESH_TOKEN_EXPIRATION);
    }

    private String generateToken(String email, long expirationTime) {
        return generateToken(email, null, expirationTime);
    }

    private String generateToken(String email, String role, long expirationTime) {
        var builder = Jwts.builder()
                .setSubject(email);

        if (role != null && !role.isBlank()) {
            builder.claim("role", role);
        }

        return builder
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractEmail(String token) {
        return extractClaims(token)
                .getSubject();
    }

    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public long getAccessTokenExpirationSeconds() {
        return ACCESS_TOKEN_EXPIRATION / 1000;
    }
}
