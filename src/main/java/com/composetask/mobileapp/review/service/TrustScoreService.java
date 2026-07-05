package com.composetask.mobileapp.review.service;

import com.composetask.mobileapp.common.exception.ResourceNotFoundException;
import com.composetask.mobileapp.review.dto.TrustScoreResponse;
import com.composetask.mobileapp.review.repository.ReviewRepository;
import com.composetask.mobileapp.vendor.model.Vendor;
import com.composetask.mobileapp.vendor.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service to calculate trust score for vendors based on reviews.
 */
@Service
@RequiredArgsConstructor
public class TrustScoreService {

    private final ReviewRepository reviewRepository;
    private final VendorRepository vendorRepository;

    /**
     * Get trust score for a vendor.
     * Includes: average rating, total review count, verification status, and computed score.
     */
    public TrustScoreResponse getTrustScore(Long vendorId) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));

        Double averageRating = reviewRepository.getAverageRatingForVendor(vendorId);
        Long totalReviews = reviewRepository.countReviewsForVendor(vendorId);

        // Handle null average (no reviews yet)
        if (averageRating == null) {
            averageRating = 0.0;
        }

        // Calculate trust score: weighted average of rating (60%) and verification (40%)
        double ratingScore = (averageRating / 5.0) * 100; // Normalize to 0-100
        double verificationBonus = vendor.isVerified() ? 100 : 50; // Verified: 100, Unverified: 50
        double trustScore = (ratingScore * 0.6) + (verificationBonus * 0.4); // Weighted average
        trustScore = Math.min(100, Math.max(0, trustScore)); // Clamp to 0-100

        return TrustScoreResponse.builder()
                .vendorId(vendorId)
                .vendorName(vendor.getBusinessName())
                .averageRating(Math.round(averageRating * 100.0) / 100.0) // Round to 2 decimals
                .totalReviews(totalReviews)
                .isVerified(vendor.isVerified())
                .trustScore(Math.round(trustScore * 100.0) / 100.0) // Round to 2 decimals
                .build();
    }

    /**
     * Get average rating for a vendor (helper method).
     */
    public Double getAverageRating(Long vendorId) {
        Double avg = reviewRepository.getAverageRatingForVendor(vendorId);
        return avg == null ? 0.0 : Math.round(avg * 100.0) / 100.0;
    }

    /**
     * Get total review count for a vendor (helper method).
     */
    public Long getTotalReviewCount(Long vendorId) {
        Long count = reviewRepository.countReviewsForVendor(vendorId);
        return count == null ? 0L : count;
    }
}

