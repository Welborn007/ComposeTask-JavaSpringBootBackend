package com.composetask.mobileapp.userapi.service;

import com.composetask.mobileapp.Constants;
import com.composetask.mobileapp.common.exception.ResourceNotFoundException;
import com.composetask.mobileapp.common.exception.UnauthorizedException;
import com.composetask.mobileapp.userapi.dto.UpdateUserRequest;
import com.composetask.mobileapp.userapi.dto.UserResponse;
import com.composetask.mobileapp.userapi.model.User;
import com.composetask.mobileapp.userapi.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToUserResponse)
                .toList();
    }

    // ✅ Get current authenticated user (from JWT)
    public UserResponse getCurrentUser() {
        String email = getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
        return mapToUserResponse(user);
    }

    // ✅ Update current authenticated user (no id required)
    public UserResponse updateCurrentUser(UpdateUserRequest request) {
        String email = getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));

        // assertSelfOrAdmin not necessary since this is the current user, but keep admin ability
        assertSelfOrAdmin(user);

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        User saved = userRepository.save(user);
        return mapToUserResponse(saved);
    }

    // ✅ Delete current authenticated user (no id required)
    public void deleteCurrentUser() {
        String email = getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
        userRepository.deleteById(user.getId());
    }

    // ✅ Update role for current authenticated user (no id required)
    public UserResponse updateCurrentUserRole(String roleName) {
        String email = getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));

        try {
            Constants.Role role = Constants.Role.valueOf(roleName);
            user.setRole(role);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid role: " + roleName);
        }

        User saved = userRepository.save(user);
        return mapToUserResponse(saved);
    }

    private void assertSelfOrAdmin(User targetUser) {
        String requesterEmail = getCurrentUserEmail();
        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));

        boolean isSelf = targetUser.getId().equals(requester.getId());
        boolean isAdmin = requester.getRole() == Constants.Role.ADMIN;

        if (!isSelf && !isAdmin) {
            throw new UnauthorizedException("You are not allowed to access this user");
        }
    }

    private String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    // 🔁 Mapper (private)
    private UserResponse mapToUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole() != null ? user.getRole().name() : null
        );
    }
}