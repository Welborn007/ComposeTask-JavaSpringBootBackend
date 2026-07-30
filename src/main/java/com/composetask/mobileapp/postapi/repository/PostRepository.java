package com.composetask.mobileapp.postapi.repository;

import com.composetask.mobileapp.postapi.model.Post;
import com.composetask.mobileapp.userapi.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {
    Page<Post> findByUser(User user, Pageable pageable);
}