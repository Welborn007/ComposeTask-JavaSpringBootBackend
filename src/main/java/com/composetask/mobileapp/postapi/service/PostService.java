package com.composetask.mobileapp.postapi.service;

import com.composetask.mobileapp.common.dto.PageResponse;
import com.composetask.mobileapp.common.exception.ResourceNotFoundException;
import com.composetask.mobileapp.common.exception.UnauthorizedException;
import com.composetask.mobileapp.config.JwtUtil;
import com.composetask.mobileapp.postapi.dto.CreatePostRequest;
import com.composetask.mobileapp.postapi.dto.PostResponse;
import com.composetask.mobileapp.postapi.dto.UpdatePostRequest;
import com.composetask.mobileapp.postapi.dto.UserSummary;
import com.composetask.mobileapp.postapi.model.Post;
import com.composetask.mobileapp.postapi.repository.PostRepository;
import com.composetask.mobileapp.userapi.model.User;
import com.composetask.mobileapp.userapi.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setUser(user);

        return mapToResponse(postRepository.save(post));
    }

    // 🌐 Get all posts
    public PageResponse<PostResponse> getAllPosts(Pageable pageable) {

        Page<Post> page = postRepository.findAll(pageable);

        List<PostResponse> content = page.getContent()
                .stream()
                .map(this::mapToResponse)
                .toList();

        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    // 🔐 Get my posts
    public PageResponse<PostResponse> getMyPosts(String token, Pageable pageable) {

        String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Page<Post> page = postRepository.findByUser(user, pageable);

        List<PostResponse> content = page.getContent()
                .stream()
                .map(this::mapToResponse)
                .toList();

        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
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

    public PostResponse updatePost(
            Long postId,
            UpdatePostRequest request,
            String token
    ) {
        String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        // 🔥 OWNERSHIP CHECK
        if (!post.getUser().getEmail().equals(email)) {
            throw new UnauthorizedException("You are not allowed to update this post");
        }

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());

        Post updated = postRepository.save(post);
        return mapToResponse(updated);
    }

    public void deletePost(Long postId, String token) {
        String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        // 🔥 OWNERSHIP CHECK
        if (!post.getUser().getEmail().equals(email)) {
            throw new UnauthorizedException("You are not allowed to delete this post");
        }

        postRepository.delete(post);
    }

}
