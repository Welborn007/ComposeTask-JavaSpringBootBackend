package com.composetask.mobileapp.vendor.model;

/**
 * Simple Vendor model.
 */

import com.composetask.mobileapp.userapi.model.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vendor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String businessName;

    private String description;

    private String category; // plumber, electrician, etc.

    private String location; // Mumbai, Delhi

    @Column(unique = true)
    private String gstNumber;

    private boolean verified = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    private LocalDateTime createdAt;
}