package com.team12.matchingplatform.match.service;

import com.team12.matchingplatform.match.dto.MatchResult;
import com.team12.matchingplatform.match.dto.ProjectRequirement;
import com.team12.matchingplatform.profile.entity.FreelancerProfile;

import java.util.List;

public interface AiMatchingService {
    List<MatchResult> findMatches(ProjectRequirement requirement, List<FreelancerProfile> freelancerPool);
}
