package com.composetask.mobileapp.postapi.controller;

import com.composetask.mobileapp.common.dto.ApiResponse;
import com.composetask.mobileapp.common.dto.PageResponse;
import com.composetask.mobileapp.postapi.dto.CreatePostRequest;
import com.composetask.mobileapp.postapi.dto.PostResponse;
import com.composetask.mobileapp.postapi.dto.UpdatePostRequest;
import com.composetask.mobileapp.postapi.model.Post;
import com.composetask.mobileapp.postapi.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 🔒 Requires JWT
    @PostMapping
    public ResponseEntity<ApiResponse<PostResponse>> createPost(
            @Valid @RequestBody CreatePostRequest request,
            @RequestHeader("Authorization") String token
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        postService.createPost(request, token),
                        "Post created successfully"
                ));
    }

    // 🌐 Public
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<PostResponse>>> getAllPosts(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        postService.getAllPosts(pageable),
                        "Posts fetched successfully"
                )
        );
    }

    // 🔐 My posts
    @GetMapping("/my")
    public ResponseEntity<PageResponse<PostResponse>> getMyPosts(
            @RequestHeader("Authorization") String token,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(postService.getMyPosts(token, pageable));
    }

    // 🔒 Update post (owner only)
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PostResponse>>updatePost(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePostRequest request,
            @RequestHeader("Authorization") String token
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        postService.updatePost(id, request, token),
                        "Post updated successfully"
                )
        );
    }

    // 🔒 Delete post (owner only)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable UUID id,
            @RequestHeader("Authorization") String token
    ) {
        postService.deletePost(id, token);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Post deleted successfully")
        );
    }

}