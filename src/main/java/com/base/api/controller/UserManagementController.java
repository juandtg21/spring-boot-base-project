package com.base.api.controller;

import com.base.api.dto.SignUpRequest;
import com.base.api.dto.UserInfo;
import com.base.api.exception.UserAlreadyExistAuthenticationException;
import com.base.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "Admin and self-service user operations")
@PreAuthorize("hasRole('ADMIN')")
public class UserManagementController {

    private final UserService userService;

    public UserManagementController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get user by ID")
    @ApiResponse(responseCode = "200", description = "User found")
    @ApiResponse(responseCode = "404", description = "User not found")
    @GetMapping("/{id}")
    public ResponseEntity<UserInfo> getUserById(@PathVariable Long id) {
        UserInfo userInfo = userService.getUserById(id);
        return ResponseEntity.ok(userInfo);
    }

    @Operation(summary = "Get all users")
    @ApiResponse(responseCode = "200", description = "List of users retrieved")
    @GetMapping
    public ResponseEntity<List<UserInfo>> findAllUsers() {
        List<UserInfo> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Create new user")
    @ApiResponse(responseCode = "201", description = "User created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        try {
            userService.registerNewUser(signUpRequest);
        } catch (UserAlreadyExistAuthenticationException e) {
            log.error("Exception Occurred", e);
            return new ResponseEntity<>(new com.base.api.dto.ApiResponse(false, "Email Address already in use!"), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok().body(new com.base.api.dto.ApiResponse(true, "User registered successfully"));
    }

    @Operation(summary = "Update existing user")
    @ApiResponse(responseCode = "200", description = "User updated successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @PutMapping("/{id}")
    public ResponseEntity<UserInfo> updateUser(@PathVariable Long id, @RequestBody @Valid UserInfo userInfo) {
        UserInfo updatedUser = userService.updateUser(id, userInfo);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Delete user")
    @ApiResponse(responseCode = "204", description = "User deleted successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
