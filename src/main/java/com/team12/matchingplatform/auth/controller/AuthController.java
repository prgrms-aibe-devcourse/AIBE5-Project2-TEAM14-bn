package com.team12.matchingplatform.auth.controller;

import com.team12.matchingplatform.auth.dto.LoginRequest;
import com.team12.matchingplatform.auth.dto.SignupRequest;
import com.team12.matchingplatform.auth.entity.User;
import com.team12.matchingplatform.auth.service.AuthService;
import com.team12.matchingplatform.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<String>> signup(@Valid @RequestBody SignupRequest request) {
        User user = authService.signup(request);
        return ResponseEntity.ok(ApiResponse.success("User registered successfully: " + user.getEmail()));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Login handled by Spring Security"));
    }
}
