package com.team12.matchingplatform.project.service;

import com.team12.matchingplatform.auth.entity.User;
import com.team12.matchingplatform.auth.repository.UserRepository;
import com.team12.matchingplatform.common.exception.ResourceNotFoundException;
import com.team12.matchingplatform.project.dto.ProjectRequest;
import com.team12.matchingplatform.project.entity.Project;
import com.team12.matchingplatform.project.entity.ProjectStatus;
import com.team12.matchingplatform.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Transactional
    public Project createProject(Long userId, ProjectRequest request) {
        User client = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        Project project = Project.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .requiredSkills(request.getRequiredSkills())
                .budget(request.getBudget())
                .status(ProjectStatus.OPEN)
                .client(client)
                .build();
        return projectRepository.save(project);
    }

    @Transactional(readOnly = true)
    public Project getProject(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));
    }

    @Transactional(readOnly = true)
    public List<Project> getProjectsByClient(Long clientId) {
        return projectRepository.findByClientId(clientId);
    }

    @Transactional
    public Project updateStatus(Long id, ProjectStatus status) {
        Project project = getProject(id);
        project.setStatus(status);
        return projectRepository.save(project);
    }

    @Transactional
    public void deleteProject(Long id) {
        Project project = getProject(id);
        projectRepository.delete(project);
    }
}
