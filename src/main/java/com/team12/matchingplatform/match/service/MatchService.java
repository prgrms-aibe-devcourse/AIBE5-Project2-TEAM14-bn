package com.team12.matchingplatform.match.service;

import com.team12.matchingplatform.common.exception.ResourceNotFoundException;
import com.team12.matchingplatform.match.dto.MatchResult;
import com.team12.matchingplatform.match.dto.ProjectRequirement;
import com.team12.matchingplatform.profile.entity.FreelancerProfile;
import com.team12.matchingplatform.profile.repository.FreelancerProfileRepository;
import com.team12.matchingplatform.project.entity.Project;
import com.team12.matchingplatform.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final FreelancerProfileRepository profileRepository;
    private final ProjectRepository projectRepository;
    private final AiMatchingService aiMatchingService;

    @Transactional(readOnly = true)
    public List<FreelancerProfile> searchByKeyword(String keyword) {
        return profileRepository.findAll().stream()
                .filter(p -> p.getSkills() != null &&
                        p.getSkills().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MatchResult> getAiMatches(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        List<String> skills = project.getRequiredSkills() != null
                ? Arrays.asList(project.getRequiredSkills().split(","))
                : List.of();

        ProjectRequirement requirement = ProjectRequirement.builder()
                .title(project.getTitle())
                .description(project.getDescription())
                .requiredSkills(skills)
                .budget(project.getBudget())
                .build();

        List<FreelancerProfile> freelancers = profileRepository.findAll();
        return aiMatchingService.findMatches(requirement, freelancers);
    }
}
