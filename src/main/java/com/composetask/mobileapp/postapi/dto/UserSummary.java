package com.composetask.mobileapp.postapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class UserSummary {
    private UUID id;
    private String name;
}