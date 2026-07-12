package com.composetask.mobileapp;

import com.composetask.mobileapp.common.exception.BadRequestException;
import com.composetask.mobileapp.common.exception.UnauthorizedException;
import com.composetask.mobileapp.config.JwtUtil;
import com.composetask.mobileapp.review.service.TrustScoreService;
import com.composetask.mobileapp.userapi.model.User;
import com.composetask.mobileapp.userapi.repository.UserRepository;
import com.composetask.mobileapp.vendor.dto.CreateVendorRequest;
import com.composetask.mobileapp.vendor.repository.VendorRepository;
import com.composetask.mobileapp.vendor.service.VendorService;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VendorServiceRbacTest {

    private final VendorRepository vendorRepository = mock(VendorRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final JwtUtil jwtUtil = mock(JwtUtil.class);
    private final TrustScoreService trustScoreService = mock(TrustScoreService.class);
    private final VendorService vendorService = new VendorService(
            vendorRepository,
            userRepository,
            jwtUtil,
            trustScoreService
    );

    @Test
    void customerCannotCreateVendorProfile() {
        User customer = User.builder()
                .id(1L)
                .email("customer@example.com")
                .role(Constants.Role.CUSTOMER)
                .build();

        when(jwtUtil.extractEmail("token")).thenReturn(customer.getEmail());
        when(userRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));

        assertThrows(
                UnauthorizedException.class,
                () -> vendorService.createVendor(new CreateVendorRequest(), "Bearer token")
        );
    }

    @Test
    void vendorCannotCreateSecondVendorProfile() {
        User vendor = User.builder()
                .id(2L)
                .email("vendor@example.com")
                .role(Constants.Role.VENDOR)
                .build();

        when(jwtUtil.extractEmail("token")).thenReturn(vendor.getEmail());
        when(userRepository.findByEmail(vendor.getEmail())).thenReturn(Optional.of(vendor));
        when(vendorRepository.existsByUserId(vendor.getId())).thenReturn(true);

        assertThrows(
                BadRequestException.class,
                () -> vendorService.createVendor(new CreateVendorRequest(), "Bearer token")
        );
    }
}
