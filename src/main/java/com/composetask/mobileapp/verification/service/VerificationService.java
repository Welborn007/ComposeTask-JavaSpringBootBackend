package com.composetask.mobileapp.verification.service;

import com.composetask.mobileapp.common.exception.UnauthorizedException;
import com.composetask.mobileapp.config.JwtUtil;
import com.composetask.mobileapp.vendor.model.Vendor;
import com.composetask.mobileapp.vendor.repository.VendorRepository;
import com.composetask.mobileapp.verification.dto.CreateVerificationRequest;
import com.composetask.mobileapp.verification.dto.VerificationResponse;
import com.composetask.mobileapp.verification.model.Verification;
import com.composetask.mobileapp.verification.model.VerificationStatus;
import com.composetask.mobileapp.verification.repository.VerificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VerificationService {

    private final VerificationRepository verificationRepository;
    private final VendorRepository vendorRepository;
    private final JwtUtil jwtUtil;

    // 📌 Submit document
    public VerificationResponse submitVerification(
            Long vendorId,
            CreateVerificationRequest request,
            String token
    ) {

        String email = extractEmail(token);

        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        // 🔒 Ownership check
        if (!vendor.getUser().getEmail().equals(email)) {
            throw new UnauthorizedException("Not your vendor");
        }

        Verification verification = Verification.builder()
                .documentType(request.getDocumentType())
                .documentUrl(request.getDocumentUrl())
                .status(VerificationStatus.PENDING)
                .vendor(vendor)
                .build();

        return mapToResponse(verificationRepository.save(verification));
    }

    // 📌 Get vendor verification
    public List<VerificationResponse> getVendorVerification(Long vendorId) {

        return verificationRepository.findByVendorId(vendorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // 📌 Approve (admin)
    @Transactional
    public VerificationResponse approve(Long id, String token) {

        String email = extractEmail(token);

        Verification verification = verificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Verification not found"));

        verification.setStatus(VerificationStatus.APPROVED);
        verification.setReviewedAt(LocalDateTime.now());
        verification.setReviewedBy(email); // ✅ FIX

        Vendor vendor = verification.getVendor();
        vendor.setVerified(true);

        return mapToResponse(verificationRepository.save(verification));
    }

    // 📌 Reject
    public VerificationResponse reject(Long id) {

        Verification verification = verificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Verification not found"));

        verification.setStatus(VerificationStatus.REJECTED);
        verification.setReviewedAt(LocalDateTime.now());

        return mapToResponse(verificationRepository.save(verification));
    }

    // 🔑 Helper
    private String extractEmail(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new UnauthorizedException("Invalid token");
        }
        return jwtUtil.extractEmail(token.substring(7));
    }

    private VerificationResponse mapToResponse(Verification v) {
        return VerificationResponse.builder()
                .id(v.getId())
                .documentType(v.getDocumentType())
                .documentUrl(v.getDocumentUrl())
                .status(v.getStatus())
                .reviewedBy(v.getReviewedBy())
                .reviewedAt(v.getReviewedAt())
                .createdAt(v.getCreatedAt())
                .build();
    }
}

