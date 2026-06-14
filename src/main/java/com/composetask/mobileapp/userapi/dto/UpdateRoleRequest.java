package com.composetask.mobileapp.userapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateRoleRequest {
    @NotBlank
    private String role; // Expected values: USER, ADMIN, VENDOR, CUSTOMER
}

