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

@RestController
@RequestMapping("/api/verification")
@RequiredArgsConstructor
public class VerificationController {

    private final VerificationService verificationService;

    @PostMapping("/{vendorId}")
    public ResponseEntity<ApiResponse<VerificationResponse>> submit(
            @PathVariable Long vendorId,
            @Valid @RequestBody CreateVerificationRequest request,
            @RequestHeader("Authorization") String token
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        verificationService.submitVerification(vendorId, request, token),
                        "Document submitted"
                )
        );
    }

    @GetMapping("/{vendorId}")
    public ResponseEntity<ApiResponse<List<VerificationResponse>>> get(
            @PathVariable Long vendorId,
            @RequestHeader("Authorization") String token
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        verificationService.getVendorVerification(vendorId, token),
                        "Verification fetched"
                )
        );
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<VerificationResponse>> approve(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        verificationService.approve(id, token),
                        "Approved"
                )
        );
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<VerificationResponse>> reject(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        verificationService.reject(id, token),
                        "Rejected"
                )
        );
    }
}
