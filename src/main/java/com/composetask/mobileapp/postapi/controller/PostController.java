package com.composetask.mobileapp.postapi.controller;

import com.composetask.mobileapp.postapi.dto.CreatePostRequest;
import com.composetask.mobileapp.postapi.dto.PostResponse;
import com.composetask.mobileapp.postapi.model.Post;
import com.composetask.mobileapp.postapi.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 🔒 Requires JWT
    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @RequestBody @Valid CreatePostRequest request,
            @RequestHeader("Authorization") String token
    ) {
        return ResponseEntity.ok(postService.createPost(request, token));
    }

    // 🌐 Public
    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    // 🔐 My posts
    @GetMapping("/my")
    public ResponseEntity<List<PostResponse>> getMyPosts(
            @RequestHeader("Authorization") String token
    ) {
        return ResponseEntity.ok(postService.getMyPosts(token));
    }
}
