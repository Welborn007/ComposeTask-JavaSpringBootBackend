package com.composetask.mobileapp.userapi.controller;

import com.composetask.mobileapp.userapi.dto.UpdateUserRequest;
import com.composetask.mobileapp.userapi.dto.UserResponse;
// ...existing imports...
import com.composetask.mobileapp.userapi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // Use JWT-authenticated user (no id in path)
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        UserResponse user = userService.getCurrentUser();
        return ResponseEntity.ok(user);
    }

    @PutMapping
    public ResponseEntity<UserResponse> updateCurrentUser(@RequestBody @Valid UpdateUserRequest request) {
        UserResponse updated = userService.updateCurrentUser(request);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/role")
    public ResponseEntity<UserResponse> updateCurrentUserRole(@RequestBody @Valid com.composetask.mobileapp.userapi.dto.UpdateRoleRequest request) {
        UserResponse updated = userService.updateCurrentUserRole(request.getRole());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteCurrentUser() {
        userService.deleteCurrentUser();
        return ResponseEntity.noContent().build();
    }
}