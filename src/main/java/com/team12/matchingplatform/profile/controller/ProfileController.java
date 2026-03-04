package com.team12.matchingplatform.profile.controller;

import com.team12.matchingplatform.common.response.ApiResponse;
import com.team12.matchingplatform.profile.dto.ProfileRequest;
import com.team12.matchingplatform.profile.entity.FreelancerProfile;
import com.team12.matchingplatform.profile.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/{userId}")
    public ResponseEntity<ApiResponse<FreelancerProfile>> createProfile(
            @PathVariable Long userId,
            @Valid @RequestBody ProfileRequest request) {
        FreelancerProfile profile = profileService.createProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<FreelancerProfile>> getProfile(@PathVariable Long userId) {
        FreelancerProfile profile = profileService.getProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<FreelancerProfile>> updateProfile(
            @PathVariable Long userId,
            @Valid @RequestBody ProfileRequest request) {
        FreelancerProfile profile = profileService.updateProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.success(profile));
    }
}
