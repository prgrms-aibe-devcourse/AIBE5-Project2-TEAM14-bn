package com.team12.matchingplatform.review.controller;

import com.team12.matchingplatform.common.response.ApiResponse;
import com.team12.matchingplatform.review.dto.ReviewRequest;
import com.team12.matchingplatform.review.entity.Review;
import com.team12.matchingplatform.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ApiResponse<Review>> createReview(
            @RequestParam Long reviewerId,
            @Valid @RequestBody ReviewRequest request) {
        Review review = reviewService.createReview(reviewerId, request);
        return ResponseEntity.ok(ApiResponse.success(review));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Review>>> getReviewsForUser(@PathVariable Long userId) {
        List<Review> reviews = reviewService.getReviewsForUser(userId);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }
}
