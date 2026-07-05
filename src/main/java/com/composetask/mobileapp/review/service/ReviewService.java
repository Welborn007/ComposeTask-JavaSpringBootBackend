package com.composetask.mobileapp.review.service;

import com.composetask.mobileapp.common.dto.PageResponse;
import com.composetask.mobileapp.common.exception.UnauthorizedException;
import com.composetask.mobileapp.common.exception.BadRequestException;
import com.composetask.mobileapp.common.exception.ResourceNotFoundException;
import com.composetask.mobileapp.config.JwtUtil;
import com.composetask.mobileapp.review.dto.ReviewRequest;
import com.composetask.mobileapp.review.dto.ReviewResponse;
import com.composetask.mobileapp.review.model.Review;
import com.composetask.mobileapp.review.repository.ReviewRepository;
import com.composetask.mobileapp.userapi.model.User;
import com.composetask.mobileapp.userapi.repository.UserRepository;
import com.composetask.mobileapp.vendor.model.Vendor;
import com.composetask.mobileapp.vendor.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final VendorRepository vendorRepository;

    // CREATE
    public ReviewResponse createReview(ReviewRequest request, String token) {

        String email = extractEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Vendor vendor = vendorRepository.findById(request.getVendorId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));

        // Prevent customers from reviewing their own vendor profile
        if (vendor.getUser() != null && vendor.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Cannot review your own vendor profile");
        }

        // Prevent duplicate reviews by same customer for same vendor
        boolean already = reviewRepository.hasCustomerReviewedVendor(vendor.getId(), user.getId());
        if (already) {
            throw new BadRequestException("Customer has already reviewed this vendor");
        }

        Review review = Review.builder()
                .title(request.getTitle())
                .comment(request.getComment())
                .rating(request.getRating())
                .vendor(vendor)
                .customer(user)
                .build();

        return mapToResponse(reviewRepository.save(review));
    }

    public PageResponse<ReviewResponse> getReviewsByVendor(Long vendorId, Pageable pageable) {
        Page<Review> page = reviewRepository.findByVendorId(vendorId, pageable);
        List<ReviewResponse> content = page.getContent().stream().map(this::mapToResponse).toList();
        return new PageResponse<>(content, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
    }

    public ReviewResponse getReviewById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
        return mapToResponse(review);
    }

    public PageResponse<ReviewResponse> getMyReviews(String token, Pageable pageable) {
        String email = extractEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Page<Review> page = reviewRepository.findByCustomerId(user.getId(), pageable);
        List<ReviewResponse> content = page.getContent().stream().map(this::mapToResponse).toList();
        return new PageResponse<>(content, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
    }

    public ReviewResponse updateReview(Long id, ReviewRequest request, String token) {
        String email = extractEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (!review.getCustomer().getId().equals(user.getId())) {
            throw new UnauthorizedException("You don't have permission to update this review");
        }

        review.setTitle(request.getTitle());
        review.setComment(request.getComment());
        review.setRating(request.getRating());

        return mapToResponse(reviewRepository.save(review));
    }

    public void deleteReview(Long id, String token) {
        String email = extractEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        // Allow admin to delete any review
        boolean isAdmin = user.getRole() != null && user.getRole().name().equals("ADMIN");
        if (!review.getCustomer().getId().equals(user.getId()) && !isAdmin) {
            throw new UnauthorizedException("You don't have permission to delete this review");
        }

        reviewRepository.deleteById(id);
    }

    public Double getAverageRating(Long vendorId) {
        Double avg = reviewRepository.getAverageRatingForVendor(vendorId);
        return avg == null ? 0.0 : avg;
    }

    // 🔑 helper
    private String extractEmail(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new UnauthorizedException("Invalid token");
        }
        return jwtUtil.extractEmail(token.substring(7));
    }

    private ReviewResponse mapToResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .title(review.getTitle())
                .comment(review.getComment())
                .rating(review.getRating())
                .vendorId(review.getVendor().getId())
                .vendorName(review.getVendor().getBusinessName())
                .customerId(review.getCustomer().getId())
                .customerName(review.getCustomer().getName())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}