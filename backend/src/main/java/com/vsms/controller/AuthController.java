package com.vsms.controller;

import com.vsms.dto.ApiResponse;
import com.vsms.dto.LoginRequest;
import com.vsms.dto.RegisterRequest;
import com.vsms.model.User;
import com.vsms.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Register a new user (Customer by default, or Admin)
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> register(@Valid @RequestBody RegisterRequest request) {
        User createdUser = authService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registration successful! You can now log in.", createdUser));
    }

    /**
     * User login with email and password
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<User>> login(@Valid @RequestBody LoginRequest request) {
        User user = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful!", user));
    }
}
