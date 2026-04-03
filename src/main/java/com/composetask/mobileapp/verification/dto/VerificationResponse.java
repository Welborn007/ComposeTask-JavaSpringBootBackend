package com.composetask.mobileapp.verification.dto;

import com.composetask.mobileapp.verification.model.VerificationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class VerificationResponse {

    private Long id;
    private String documentType;
    private String documentUrl;
    private VerificationStatus status;
    private String reviewedBy;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;
}

