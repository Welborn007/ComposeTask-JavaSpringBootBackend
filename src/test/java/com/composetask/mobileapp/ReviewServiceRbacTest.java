package com.composetask.mobileapp;

import com.composetask.mobileapp.common.exception.UnauthorizedException;
import com.composetask.mobileapp.config.JwtUtil;
import com.composetask.mobileapp.review.dto.ReviewRequest;
import com.composetask.mobileapp.review.repository.ReviewRepository;
import com.composetask.mobileapp.review.service.ReviewService;
import com.composetask.mobileapp.userapi.model.User;
import com.composetask.mobileapp.userapi.repository.UserRepository;
import com.composetask.mobileapp.vendor.repository.VendorRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReviewServiceRbacTest {

    private final ReviewRepository reviewRepository = mock(ReviewRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final JwtUtil jwtUtil = mock(JwtUtil.class);
    private final VendorRepository vendorRepository = mock(VendorRepository.class);
    private final ReviewService reviewService = new ReviewService(
            reviewRepository,
            userRepository,
            jwtUtil,
            vendorRepository
    );

    @Test
    void vendorCannotCreateReview() {
        User vendor = User.builder()
                .id(1L)
                .email("vendor@example.com")
                .role(Constants.Role.VENDOR)
                .build();

        when(jwtUtil.extractEmail("token")).thenReturn(vendor.getEmail());
        when(userRepository.findByEmail(vendor.getEmail())).thenReturn(Optional.of(vendor));

        assertThrows(
                UnauthorizedException.class,
                () -> reviewService.createReview(new ReviewRequest(), "Bearer token")
        );
    }
}
