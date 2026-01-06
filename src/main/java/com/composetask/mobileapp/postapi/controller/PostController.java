package com.composetask.mobileapp.postapi.controller;

import com.composetask.mobileapp.postapi.model.Post;
import com.composetask.mobileapp.postapi.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // 🔒 Requires JWT (secured)
    @PostMapping
    public ResponseEntity<Post> createPost(
            @RequestBody Post post,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(postService.createPost(post,token));
    }

    // 🌐 Public - anyone can read
    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @GetMapping("/my")
    public ResponseEntity<List<Post>> getMyPosts(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(postService.getMyPosts(token));
    }

}