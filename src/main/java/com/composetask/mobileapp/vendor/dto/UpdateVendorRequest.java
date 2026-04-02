package com.composetask.mobileapp.vendor.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateVendorRequest {

    @NotBlank
    private String businessName;

    @NotBlank
    private String description;

    @NotBlank
    private String category;

    @NotBlank
    private String location;
}
