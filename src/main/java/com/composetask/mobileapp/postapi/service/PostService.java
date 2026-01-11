package com.composetask.mobileapp.postapi.service;

import com.composetask.mobileapp.config.JwtUtil;
import com.composetask.mobileapp.postapi.dto.CreatePostRequest;
import com.composetask.mobileapp.postapi.dto.PostResponse;
import com.composetask.mobileapp.postapi.dto.UserSummary;
import com.composetask.mobileapp.postapi.model.Post;
import com.composetask.mobileapp.postapi.repository.PostRepository;
import com.composetask.mobileapp.userapi.model.User;
import com.composetask.mobileapp.userapi.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public PostService(PostRepository postRepository, UserRepository userRepository, JwtUtil jwtUtil) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    // 🔒 Create Post
    public PostResponse createPost(CreatePostRequest request, String token) {

        String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setUser(user);

        return mapToResponse(postRepository.save(post));
    }

    // 🌐 Get all posts
    public List<PostResponse> getAllPosts() {
        return postRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // 🔐 Get my posts
    public List<PostResponse> getMyPosts(String token) {

        String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return postRepository.findByUser(user)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // 🔁 Mapper
    private PostResponse mapToResponse(Post post) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                new UserSummary(
                        post.getUser().getId(),
                        post.getUser().getName()
                )
        );
    }

}
