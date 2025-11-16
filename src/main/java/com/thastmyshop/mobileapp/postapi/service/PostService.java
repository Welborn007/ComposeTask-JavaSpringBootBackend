package com.thastmyshop.mobileapp.postapi.service;

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

    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public Post createPost(Post post) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        post.setUser(user);
        return postRepository.save(post);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }
}
