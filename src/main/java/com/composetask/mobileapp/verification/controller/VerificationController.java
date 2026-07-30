package com.composetask.mobileapp.verification.controller;

import com.composetask.mobileapp.common.dto.ApiResponse;
import com.composetask.mobileapp.verification.dto.CreateVerificationRequest;
import com.composetask.mobileapp.verification.dto.VerificationResponse;
import com.composetask.mobileapp.verification.service.VerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/verification")
@RequiredArgsConstructor
public class VerificationController {

    private final VerificationService verificationService;

    @PostMapping("/{vendorId}")
    public ResponseEntity<ApiResponse<VerificationResponse>> submit(
            @PathVariable UUID vendorId,
            @Valid @RequestBody CreateVerificationRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        verificationService.submitVerification(vendorId, request),
                        "Document submitted"
                )
        );
    }

    @GetMapping("/{vendorId}")
    public ResponseEntity<ApiResponse<List<VerificationResponse>>> get(
            @PathVariable UUID vendorId
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        verificationService.getVendorVerification(vendorId),
                        "Verification fetched"
                )
        );
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<VerificationResponse>> approve(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        verificationService.approve(id),
                        "Approved"
                )
        );
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<VerificationResponse>> reject(@PathVariable UUID id) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        verificationService.reject(id),
                        "Rejected"
                )
        );
    }
}