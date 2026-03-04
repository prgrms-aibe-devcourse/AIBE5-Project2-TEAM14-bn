package com.team12.matchingplatform.match.service;

import com.team12.matchingplatform.match.dto.MatchResult;
import com.team12.matchingplatform.match.dto.ProjectRequirement;
import com.team12.matchingplatform.profile.entity.FreelancerProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiMatchingServiceImpl implements AiMatchingService {

    private final RestTemplate restTemplate;

    @Value("${ai.endpoint:}")
    private String aiEndpoint;

    @Value("${ai.api-key:}")
    private String aiApiKey;

    @Override
    public List<MatchResult> findMatches(ProjectRequirement requirement, List<FreelancerProfile> freelancerPool) {
        if (aiEndpoint == null || aiEndpoint.isBlank()) {
            log.info("AI endpoint not configured. Returning stub response.");
            return Collections.emptyList();
        }
        try {
            log.info("Calling AI endpoint: {}", aiEndpoint);
            // TODO: Implement actual AI call with proper request/response mapping
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Failed to call AI endpoint: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}
