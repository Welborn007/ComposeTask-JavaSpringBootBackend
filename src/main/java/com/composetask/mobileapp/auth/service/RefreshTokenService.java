package com.composetask.mobileapp.auth.service;

import com.composetask.mobileapp.auth.model.RefreshToken;
import com.composetask.mobileapp.auth.repository.RefreshTokenRepository;
import com.composetask.mobileapp.common.exception.UnauthorizedException;
import com.composetask.mobileapp.userapi.model.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh.expiration:604800000}")  // 7 days in ms
    private long REFRESH_TOKEN_EXPIRATION;

    public RefreshToken createRefreshToken(User user) {
        // Revoke any existing refresh tokens for this user
        refreshTokenRepository.revokeAllByUserId(user.getId());

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiryDate(LocalDateTime.now().plusSeconds(REFRESH_TOKEN_EXPIRATION / 1000))
                .isRevoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken validateRefreshToken(String token) {
        if (!StringUtils.hasText(token)) {
            throw new UnauthorizedException("Refresh token is missing");
        }

        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new UnauthorizedException("Refresh token not found"));

        if (!refreshToken.isValid()) {
            throw new UnauthorizedException("Refresh token is invalid or expired");
        }

        return refreshToken;
    }

    @Transactional
    public void revokeRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new UnauthorizedException("Refresh token not found"));

        refreshToken.setIsRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public void revokeAllUserTokens(UUID userId) {
        refreshTokenRepository.revokeAllByUserId(userId);
    }
}