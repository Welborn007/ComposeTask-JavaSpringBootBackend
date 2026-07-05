package com.composetask.mobileapp.review.controller;

import com.composetask.mobileapp.common.dto.ApiResponse;
import com.composetask.mobileapp.common.dto.PageResponse;
import com.composetask.mobileapp.review.dto.ReviewRequest;
import com.composetask.mobileapp.review.dto.ReviewResponse;
import com.composetask.mobileapp.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @Valid @RequestBody ReviewRequest request,
            @RequestHeader("Authorization") String token
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        reviewService.createReview(request, token),
                        "Review created successfully"
                ));
    }

    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> getReviewsByVendor(
            @PathVariable Long vendorId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                reviewService.getReviewsByVendor(vendorId, pageable),
                "Reviews fetched successfully"
        ));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> getMyReviews(
            @RequestHeader("Authorization") String token,
            Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                reviewService.getMyReviews(token, pageable),
                "My reviews fetched successfully"
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReviewResponse>> getReview(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                reviewService.getReviewById(id),
                "Review fetched successfully"
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequest request,
            @RequestHeader("Authorization") String token
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                reviewService.updateReview(id, request, token),
                "Review updated successfully"
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token
    ) {
        reviewService.deleteReview(id, token);
        return ResponseEntity.ok(ApiResponse.success(null, "Review deleted successfully"));
    }
}

