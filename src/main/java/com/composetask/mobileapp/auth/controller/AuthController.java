package com.composetask.mobileapp.auth.controller;

import com.composetask.mobileapp.auth.dto.AuthResponse;
import com.composetask.mobileapp.auth.dto.LoginRequest;
import com.composetask.mobileapp.auth.dto.RefreshTokenRequest;
import com.composetask.mobileapp.auth.dto.LogoutRequest;
import com.composetask.mobileapp.auth.dto.SignupRequest;
import com.composetask.mobileapp.auth.service.AuthService;
import com.composetask.mobileapp.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<AuthResponse>> signup(
            @Valid @RequestBody SignupRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(authService.signup(request), "Signup successful")
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        authService.login(request),
                        "Login successful"
                )
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        AuthResponse response = authService.refreshAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(
                ApiResponse.success(response, "Access token refreshed successfully")
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @Valid @RequestBody LogoutRequest request
    ) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok(
                ApiResponse.success(null, "Logout successful")
        );
    }
}
