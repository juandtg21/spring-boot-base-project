package com.base.api.controller;

import com.base.api.dto.UserInfo;
import com.base.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@Tag(name = "User Profile", description = "User self-service operations")
@PreAuthorize("hasRole('USER')")
public class UserProfileController {

    private final UserService userService;

    public UserProfileController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get current user profile")
    @ApiResponse(responseCode = "200", description = "Profile retrieved successfully")
    @GetMapping
    public ResponseEntity<UserInfo> getCurrentUserProfile() {
        Long currentUserId = userService.getCurrentUserId();
        UserInfo userInfo = userService.getUserById(currentUserId);
        return ResponseEntity.ok(userInfo);
    }

    @Operation(summary = "Update current user profile")
    @ApiResponse(responseCode = "200", description = "Profile updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @PutMapping
    public ResponseEntity<UserInfo> updateCurrentUserProfile(@RequestBody @Valid UserInfo userInfo) {
        Long currentUserId = userService.getCurrentUserId();
        UserInfo updatedUser = userService.updateUser(currentUserId, userInfo);
        return ResponseEntity.ok(updatedUser);
    }
}
