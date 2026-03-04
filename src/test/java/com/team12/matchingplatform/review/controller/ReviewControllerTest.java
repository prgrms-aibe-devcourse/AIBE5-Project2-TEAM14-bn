package com.team12.matchingplatform.review.controller;

import com.team12.matchingplatform.auth.service.AuthService;
import com.team12.matchingplatform.config.AppConfig;
import com.team12.matchingplatform.config.SecurityConfig;
import com.team12.matchingplatform.review.entity.Review;
import com.team12.matchingplatform.review.service.ReviewService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
@Import({SecurityConfig.class, AppConfig.class})
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @MockBean
    private AuthService authService;

    @Test
    @WithMockUser
    void getReviewsForUser_returnsOk() throws Exception {
        when(reviewService.getReviewsForUser(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/reviews/user/1"))
                .andExpect(status().isOk());
    }
}
