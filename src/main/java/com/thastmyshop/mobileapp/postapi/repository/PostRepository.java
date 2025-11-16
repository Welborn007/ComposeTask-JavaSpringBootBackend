package com.thastmyshop.mobileapp.postapi.repository;

import com.thastmyshop.mobileapp.postapi.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
