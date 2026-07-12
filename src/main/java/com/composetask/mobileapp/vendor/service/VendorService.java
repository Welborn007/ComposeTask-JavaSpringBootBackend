package com.composetask.mobileapp.vendor.service;

import com.composetask.mobileapp.common.dto.PageResponse;
import com.composetask.mobileapp.common.exception.UnauthorizedException;
import com.composetask.mobileapp.config.JwtUtil;
import com.composetask.mobileapp.review.service.TrustScoreService;
import com.composetask.mobileapp.userapi.model.User;
import com.composetask.mobileapp.userapi.repository.UserRepository;
import com.composetask.mobileapp.vendor.dto.CreateVendorRequest;
import com.composetask.mobileapp.vendor.dto.UpdateVendorRequest;
import com.composetask.mobileapp.vendor.dto.VendorResponse;
import com.composetask.mobileapp.vendor.model.Vendor;
import com.composetask.mobileapp.vendor.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Placeholder service for vendor business logic.
 */
@Service
@RequiredArgsConstructor
public class VendorService {

    private final VendorRepository vendorRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final TrustScoreService trustScoreService;

    // CREATE
    public VendorResponse createVendor(CreateVendorRequest request, String token) {

        String email = extractEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Vendor vendor = Vendor.builder()
                .businessName(request.getBusinessName())
                .description(request.getDescription())
                .category(request.getCategory())
                .location(request.getLocation())
                .gstNumber(request.getGstNumber())
                .user(user)
                .build();

        return mapToResponse(vendorRepository.save(vendor));
    }

    // GET ALL / SEARCH
    public PageResponse<VendorResponse> getAllVendors(
            String search,
            String q,
            String category,
            String city,
            String location,
            Boolean verified,
            Double minRating,
            Double minTrustScore,
            Pageable pageable
    ) {
        String searchTerm = normalizeLower(search);
        if (searchTerm.isEmpty()) {
            searchTerm = normalizeLower(q);
        }

        String locationFilter = normalizeLower(city);
        if (locationFilter.isEmpty()) {
            locationFilter = normalizeLower(location);
        }

        Page<Vendor> page = vendorRepository.searchVendorsCustom(
                searchTerm.isEmpty() ? "" : searchTerm,
                normalizeLower(category),
                locationFilter.isEmpty() ? "" : locationFilter,
                verified,
                minRating,
                minTrustScore,
                pageable
        );

        List<VendorResponse> content = page.getContent()
                .stream()
                .map(this::mapToResponse)
                .toList();

        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    // UPDATE (ownership)
    public VendorResponse updateVendor(Long id, UpdateVendorRequest request, String token) {

        String email = extractEmail(token);

        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        if (!vendor.getUser().getEmail().equals(email)) {
            throw new UnauthorizedException("You are not allowed to update this vendor");
        }

        vendor.setBusinessName(request.getBusinessName());
        vendor.setDescription(request.getDescription());
        vendor.setCategory(request.getCategory());
        vendor.setLocation(request.getLocation());

        return mapToResponse(vendorRepository.save(vendor));
    }

    // DELETE
    public void deleteVendor(Long id, String token) {

        String email = extractEmail(token);

        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        if (!vendor.getUser().getEmail().equals(email)) {
            throw new UnauthorizedException("You are not allowed to delete this vendor");
        }

        vendorRepository.delete(vendor);
    }

    // MY VENDORS
    public PageResponse<VendorResponse> getMyVendors(String token, Pageable pageable) {

        String email = extractEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Page<Vendor> page = vendorRepository.findByUserId(user.getId(), pageable);

        List<VendorResponse> content = page.getContent()
                .stream()
                .map(this::mapToResponse)
                .toList();

        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    // 🔑 helper
    private String extractEmail(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new UnauthorizedException("Invalid token");
        }
        return jwtUtil.extractEmail(token.substring(7));
    }

    private String normalize(String value) {
         if (value == null || value.isBlank()) {
             return "";
         }
         return value.trim();
     }

     private String normalizeLower(String value) {
         String normalized = normalize(value);
         return normalized.isEmpty() ? "" : normalized.toLowerCase();
     }

    private VendorResponse mapToResponse(Vendor vendor) {
        Double avgRating = trustScoreService.getAverageRating(vendor.getId());
        Long totalReviews = trustScoreService.getTotalReviewCount(vendor.getId());

        return VendorResponse.builder()
                .id(vendor.getId())
                .businessName(vendor.getBusinessName())
                .description(vendor.getDescription())
                .category(vendor.getCategory())
                .location(vendor.getLocation())
                .gstNumber(vendor.getGstNumber())
                .verified(vendor.isVerified())
                .ownerEmail(vendor.getUser().getEmail())
                .createdAt(vendor.getCreatedAt())
                .averageRating(avgRating)
                .totalReviews(totalReviews)
                .build();
    }
}
