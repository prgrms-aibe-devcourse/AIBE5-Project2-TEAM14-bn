package com.team12.matchingplatform.match.controller;

import com.team12.matchingplatform.auth.service.AuthService;
import com.team12.matchingplatform.config.AppConfig;
import com.team12.matchingplatform.config.SecurityConfig;
import com.team12.matchingplatform.match.service.MatchService;
import com.team12.matchingplatform.profile.entity.FreelancerProfile;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MatchController.class)
@Import({SecurityConfig.class, AppConfig.class})
class MatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MatchService matchService;

    @MockBean
    private AuthService authService;

    @Test
    void searchByKeyword_returnsOk() throws Exception {
        when(matchService.searchByKeyword("java")).thenReturn(List.of());

        mockMvc.perform(get("/api/match/search")
                .param("keyword", "java"))
                .andExpect(status().isOk());
    }
}
