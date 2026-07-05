package com.composetask.mobileapp.review.controller;

import com.composetask.mobileapp.common.dto.ApiResponse;
import com.composetask.mobileapp.review.dto.TrustScoreResponse;
import com.composetask.mobileapp.review.service.TrustScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for trust score endpoints.
 * Exposes vendor rating and trust score information.
 */
@RestController
@RequestMapping("/api/trust-score")
@RequiredArgsConstructor
public class TrustScoreController {

    private final TrustScoreService trustScoreService;

    /**
     * Get trust score for a vendor (public endpoint).
     * Returns: average rating, total reviews, verification status, and computed trust score.
     */
    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<ApiResponse<TrustScoreResponse>> getTrustScore(@PathVariable Long vendorId) {
        TrustScoreResponse response = trustScoreService.getTrustScore(vendorId);
        return ResponseEntity.ok(ApiResponse.success(response, "Trust score fetched successfully"));
    }
}

