package com.thastmyshop.mobileapp.postapi.model;

import com.thastmyshop.mobileapp.userapi.model.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // link to the creator
}
