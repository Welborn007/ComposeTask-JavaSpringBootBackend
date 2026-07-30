package com.composetask.mobileapp.review.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ReviewResponse {

    private UUID id;
    private UUID vendorId;
    private UUID customerId;
    private String vendorName;
    private String customerName;
    private Integer rating;
    private String title;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}