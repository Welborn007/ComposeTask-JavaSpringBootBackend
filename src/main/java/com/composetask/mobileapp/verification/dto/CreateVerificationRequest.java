package com.composetask.mobileapp.verification.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateVerificationRequest {

    @NotBlank(message = "Document type is required")
    private String documentType;

    @NotBlank(message = "Document URL is required")
    private String documentUrl;
}