package com.team12.matchingplatform.project.controller;

import com.team12.matchingplatform.common.response.ApiResponse;
import com.team12.matchingplatform.project.dto.ProjectRequest;
import com.team12.matchingplatform.project.entity.Project;
import com.team12.matchingplatform.project.entity.ProjectStatus;
import com.team12.matchingplatform.project.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ApiResponse<Project>> createProject(
            @RequestParam Long userId,
            @Valid @RequestBody ProjectRequest request) {
        Project project = projectService.createProject(userId, request);
        return ResponseEntity.ok(ApiResponse.success(project));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Project>> getProject(@PathVariable Long id) {
        Project project = projectService.getProject(id);
        return ResponseEntity.ok(ApiResponse.success(project));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Project>>> getProjectsByClient(@RequestParam Long userId) {
        List<Project> projects = projectService.getProjectsByClient(userId);
        return ResponseEntity.ok(ApiResponse.success(projects));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Project>> updateStatus(
            @PathVariable Long id,
            @RequestParam ProjectStatus status) {
        Project project = projectService.updateStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(project));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
