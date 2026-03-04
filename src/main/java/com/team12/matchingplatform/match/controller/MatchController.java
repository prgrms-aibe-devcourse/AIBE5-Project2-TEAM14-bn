package com.team12.matchingplatform.match.controller;

import com.team12.matchingplatform.common.response.ApiResponse;
import com.team12.matchingplatform.match.dto.MatchResult;
import com.team12.matchingplatform.match.service.MatchService;
import com.team12.matchingplatform.profile.entity.FreelancerProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/match")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<FreelancerProfile>>> searchByKeyword(
            @RequestParam String keyword) {
        List<FreelancerProfile> results = matchService.searchByKeyword(keyword);
        return ResponseEntity.ok(ApiResponse.success(results));
    }

    @GetMapping("/ai/{projectId}")
    public ResponseEntity<ApiResponse<List<MatchResult>>> getAiMatches(
            @PathVariable Long projectId) {
        List<MatchResult> results = matchService.getAiMatches(projectId);
        return ResponseEntity.ok(ApiResponse.success(results));
    }
}
