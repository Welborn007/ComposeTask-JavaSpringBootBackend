package com.composetask.mobileapp.userapi.service;

import com.composetask.mobileapp.Constants;
import com.composetask.mobileapp.common.exception.ResourceNotFoundException;
import com.composetask.mobileapp.common.exception.UnauthorizedException;
import com.composetask.mobileapp.userapi.dto.UpdateUserRequest;
import com.composetask.mobileapp.userapi.dto.UserResponse;
import com.composetask.mobileapp.userapi.model.User;
import com.composetask.mobileapp.userapi.repository.UserRepository;
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

    public UserResponse getUserById(Long id, String requesterEmail) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        assertSelfOrAdmin(user, requesterEmail);
        return mapToUserResponse(user);
    }

    // ✅ Update user
    public UserResponse updateUser(Long id, UpdateUserRequest request, String requesterEmail) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        assertSelfOrAdmin(user, requesterEmail);
        user.setName(request.getName());
        user.setEmail(request.getEmail());

        User saved = userRepository.save(user);
        return mapToUserResponse(saved);
    }

    // ✅ Delete user
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found");
        }
        userRepository.deleteById(id);
    }

    // ✅ Update user role (admin only)
    public UserResponse updateUserRole(Long id, String roleName) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        try {
            Constants.Role role = Constants.Role.valueOf(roleName);
            user.setRole(role);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid role: " + roleName);
        }

        User saved = userRepository.save(user);
        return mapToUserResponse(saved);
    }

    private void assertSelfOrAdmin(User targetUser, String requesterEmail) {
        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));

        boolean isSelf = targetUser.getId().equals(requester.getId());
        boolean isAdmin = requester.getRole() == Constants.Role.ADMIN;

        if (!isSelf && !isAdmin) {
            throw new UnauthorizedException("You are not allowed to access this user");
        }
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
