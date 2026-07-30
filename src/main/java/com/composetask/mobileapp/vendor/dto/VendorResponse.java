package com.composetask.mobileapp.vendor.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class VendorResponse {

    private UUID id;
    private String businessName;
    private String description;
    private String category;
    private String location;
    private String gstNumber;
    private boolean verified;
    private String ownerEmail;
    private LocalDateTime createdAt;

    // Trust score fields
    private Double averageRating;    // 0-5 stars
    private Long totalReviews;       // Total number of reviews
}