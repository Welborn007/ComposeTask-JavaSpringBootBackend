package com.composetask.mobileapp.auth.service;

import com.composetask.mobileapp.common.exception.BadRequestException;
import com.composetask.mobileapp.common.exception.ResourceNotFoundException;
import com.composetask.mobileapp.common.exception.UnauthorizedException;
import com.composetask.mobileapp.config.JwtUtil;
import com.composetask.mobileapp.auth.dto.AuthResponse;
import com.composetask.mobileapp.auth.dto.LoginRequest;
import com.composetask.mobileapp.auth.dto.SignupRequest;
import com.composetask.mobileapp.auth.model.RefreshToken;
import com.composetask.mobileapp.userapi.model.User;
import com.composetask.mobileapp.Constants;
import com.composetask.mobileapp.userapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    public AuthResponse signup(SignupRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Email already registered!");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        // set role from request (VENDOR or CUSTOMER). Do not allow ADMIN signup.
        try {
            Constants.Role role = Constants.Role.valueOf(request.getRole());
            if (role == Constants.Role.ADMIN) {
                throw new BadRequestException("Cannot signup as ADMIN");
            }
            user.setRole(role);
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid role specified");
        }

        userRepository.save(user);

        return generateAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid password");
        }

        return generateAuthResponse(user);
    }

    public AuthResponse refreshAccessToken(String refreshTokenString) {
        RefreshToken refreshToken = refreshTokenService.validateRefreshToken(refreshTokenString);
        User user = refreshToken.getUser();

        String accessToken = jwtUtil.generateAccessToken(user.getEmail());
        long expiresIn = jwtUtil.getAccessTokenExpirationSeconds();

        return new AuthResponse(accessToken, "Token refreshed successfully", refreshTokenString, expiresIn);
    }

    public void logout(String refreshTokenString) {
        refreshTokenService.revokeRefreshToken(refreshTokenString);
    }

    private AuthResponse generateAuthResponse(User user) {
        String accessToken = jwtUtil.generateAccessToken(user.getEmail());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        long expiresIn = jwtUtil.getAccessTokenExpirationSeconds();

        return new AuthResponse(accessToken, "Authentication successful", refreshToken.getToken(), expiresIn);
    }
}
