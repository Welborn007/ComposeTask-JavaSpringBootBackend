package com.composetask.mobileapp.vendor.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Data transfer object for Vendor.
 */
@Data
public class CreateVendorRequest {

    @NotBlank(message = "Business name is required")
    private String businessName;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Category is required")
    private String category;

    @NotBlank(message = "Location is required")
    private String location;

    @NotBlank(message = "GST number is required")
    private String gstNumber;
}
