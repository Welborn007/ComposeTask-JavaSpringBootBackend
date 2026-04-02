package com.composetask.mobileapp.vendor.controller;

import com.composetask.mobileapp.common.dto.ApiResponse;
import com.composetask.mobileapp.common.dto.PageResponse;
import com.composetask.mobileapp.vendor.dto.CreateVendorRequest;
import com.composetask.mobileapp.vendor.dto.UpdateVendorRequest;
import com.composetask.mobileapp.vendor.dto.VendorResponse;
import com.composetask.mobileapp.vendor.service.VendorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Placeholder controller for vendor-related endpoints.
 */
@RestController
@RequestMapping("/api/vendors")
@RequiredArgsConstructor
public class VendorController {

    private final VendorService vendorService;

    @PostMapping
    public ResponseEntity<ApiResponse<VendorResponse>> createVendor(
            @Valid @RequestBody CreateVendorRequest request,
            @RequestHeader("Authorization") String token
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        vendorService.createVendor(request, token),
                        "Vendor created successfully"
                ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<VendorResponse>>> getAllVendors(
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        vendorService.getAllVendors(pageable),
                        "Vendors fetched successfully"
                )
        );
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<PageResponse<VendorResponse>>> getMyVendors(
            @RequestHeader("Authorization") String token,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        vendorService.getMyVendors(token, pageable),
                        "My vendors fetched successfully"
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VendorResponse>> updateVendor(
            @PathVariable Long id,
            @Valid @RequestBody UpdateVendorRequest request,
            @RequestHeader("Authorization") String token
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        vendorService.updateVendor(id, request, token),
                        "Vendor updated successfully"
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteVendor(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token
    ) {
        vendorService.deleteVendor(id, token);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Vendor deleted successfully")
        );
    }
}
