package com.composetask.mobileapp.userapi.controller;

import com.composetask.mobileapp.common.dto.ApiResponse;
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
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(users, "Users retrieved successfully"));
    }

    // Use JWT-authenticated user (no id in path)
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {
        UserResponse user = userService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(user, "Current user retrieved successfully"));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<UserResponse>> updateCurrentUser(@RequestBody @Valid UpdateUserRequest request) {
        UserResponse updated = userService.updateCurrentUser(request);
        return ResponseEntity.ok(ApiResponse.success(updated, "User updated successfully"));
    }

    @PutMapping("/role")
    public ResponseEntity<ApiResponse<UserResponse>> updateCurrentUserRole(@RequestBody @Valid com.composetask.mobileapp.userapi.dto.UpdateRoleRequest request) {
        UserResponse updated = userService.updateCurrentUserRole(request.getRole());
        return ResponseEntity.ok(ApiResponse.success(updated, "User role updated successfully"));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<String>> deleteCurrentUser() {
        userService.deleteCurrentUser();
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", "User deleted successfully"));
    }
}