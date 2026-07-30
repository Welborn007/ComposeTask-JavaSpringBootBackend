package com.composetask.mobileapp.userapi.model;

import com.composetask.mobileapp.Constants;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)   // ✅ store enum name instead of ordinal number
    @Builder.Default
    private Constants.Role role = Constants.Role.USER; // ✅ default role
}