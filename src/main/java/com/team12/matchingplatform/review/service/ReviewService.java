package com.team12.matchingplatform.review.service;

import com.team12.matchingplatform.auth.entity.User;
import com.team12.matchingplatform.auth.repository.UserRepository;
import com.team12.matchingplatform.common.exception.ResourceNotFoundException;
import com.team12.matchingplatform.project.entity.Project;
import com.team12.matchingplatform.project.repository.ProjectRepository;
import com.team12.matchingplatform.review.dto.ReviewRequest;
import com.team12.matchingplatform.review.entity.Review;
import com.team12.matchingplatform.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public Review createReview(Long reviewerId, ReviewRequest request) {
        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", reviewerId));
        User reviewee = userRepository.findById(request.getRevieweeId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getRevieweeId()));
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", request.getProjectId()));

        Review review = Review.builder()
                .project(project)
                .reviewer(reviewer)
                .reviewee(reviewee)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();
        return reviewRepository.save(review);
    }

    @Transactional(readOnly = true)
    public List<Review> getReviewsForUser(Long userId) {
        return reviewRepository.findByRevieweeId(userId);
    }
}
