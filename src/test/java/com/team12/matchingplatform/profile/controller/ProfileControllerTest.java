package com.team12.matchingplatform.profile.controller;

import com.team12.matchingplatform.auth.service.AuthService;
import com.team12.matchingplatform.config.AppConfig;
import com.team12.matchingplatform.config.SecurityConfig;
import com.team12.matchingplatform.profile.entity.FreelancerProfile;
import com.team12.matchingplatform.profile.service.ProfileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProfileController.class)
@Import({SecurityConfig.class, AppConfig.class})
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProfileService profileService;

    @MockBean
    private AuthService authService;

    @Test
    @WithMockUser
    void getProfile_returnsOk() throws Exception {
        FreelancerProfile profile = FreelancerProfile.builder()
                .id(1L)
                .skills("Java, Spring")
                .build();

        when(profileService.getProfile(1L)).thenReturn(profile);

        mockMvc.perform(get("/api/profiles/1"))
                .andExpect(status().isOk());
    }
}
