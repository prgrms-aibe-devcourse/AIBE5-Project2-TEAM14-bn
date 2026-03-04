package com.team12.matchingplatform.profile.service;

import com.team12.matchingplatform.auth.entity.User;
import com.team12.matchingplatform.auth.repository.UserRepository;
import com.team12.matchingplatform.common.exception.ResourceNotFoundException;
import com.team12.matchingplatform.profile.dto.ProfileRequest;
import com.team12.matchingplatform.profile.entity.FreelancerProfile;
import com.team12.matchingplatform.profile.repository.FreelancerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final FreelancerProfileRepository profileRepository;
    private final UserRepository userRepository;

    @Transactional
    public FreelancerProfile createProfile(Long userId, ProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        FreelancerProfile profile = FreelancerProfile.builder()
                .user(user)
                .bio(request.getBio())
                .skills(request.getSkills())
                .portfolioUrl(request.getPortfolioUrl())
                .hourlyRate(request.getHourlyRate())
                .build();
        return profileRepository.save(profile);
    }

    @Transactional(readOnly = true)
    public FreelancerProfile getProfile(Long userId) {
        return profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("FreelancerProfile", "userId", userId));
    }

    @Transactional
    public FreelancerProfile updateProfile(Long userId, ProfileRequest request) {
        FreelancerProfile profile = getProfile(userId);
        profile.setBio(request.getBio());
        profile.setSkills(request.getSkills());
        profile.setPortfolioUrl(request.getPortfolioUrl());
        profile.setHourlyRate(request.getHourlyRate());
        return profileRepository.save(profile);
    }
}
