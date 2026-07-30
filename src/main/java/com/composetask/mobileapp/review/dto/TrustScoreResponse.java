package com.composetask.mobileapp.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for vendor trust score response.
 * Combines average rating, review count, verification status, and computed trust score.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrustScoreResponse {

    private UUID vendorId;
    private String vendorName;
    private Double averageRating;      // 0-5 stars
    private Long totalReviews;          // Total number of reviews
    private boolean isVerified;         // Verification status
    private Double trustScore;          // 0-100, weighted (rating 60% + verification 40%)
}