package com.composetask.mobileapp.postapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class PostResponse {

    private UUID id;
    private String title;
    private String content;
    private UserSummary user;
}