package com.composetask.mobileapp.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;  // access token
    private String message;
    private String refreshToken;
    private Long expiresIn;  // in seconds
    private String tokenType;

    public AuthResponse(String token, String message) {
        this.token = token;
        this.message = message;
        this.tokenType = "Bearer";
    }

    public AuthResponse(String token, String message, String refreshToken, Long expiresIn) {
        this.token = token;
        this.message = message;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.tokenType = "Bearer";
    }
}
