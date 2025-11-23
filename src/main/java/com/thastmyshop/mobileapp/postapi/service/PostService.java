package com.thastmyshop.mobileapp.postapi.service;

import com.thastmyshop.mobileapp.config.JwtUtil;
import com.thastmyshop.mobileapp.postapi.model.Post;
import com.thastmyshop.mobileapp.postapi.repository.PostRepository;
import com.thastmyshop.mobileapp.userapi.model.User;
import com.thastmyshop.mobileapp.userapi.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
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

    public Post createPost(Post post, String token) {
        // remove "Bearer "
        token = token.replace("Bearer ", "");

        String email = jwtUtil.extractEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        post.setUser(user);
        return postRepository.save(post);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public List<Post> getMyPosts(String token) {

        token = token.replace("Bearer ", "");

        String email = jwtUtil.extractEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return postRepository.findByUser(user);
    }

}
