package com.team12.matchingplatform.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team12.matchingplatform.auth.dto.SignupRequest;
import com.team12.matchingplatform.auth.entity.User;
import com.team12.matchingplatform.auth.entity.UserRole;
import com.team12.matchingplatform.auth.service.AuthService;
import com.team12.matchingplatform.config.AppConfig;
import com.team12.matchingplatform.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, AppConfig.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    void signup_returnsOk() throws Exception {
        SignupRequest request = SignupRequest.builder()
                .email("test@example.com")
                .password("password123")
                .name("Test User")
                .role(UserRole.USER)
                .build();

        User mockUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .name("Test User")
                .role(UserRole.USER)
                .build();

        when(authService.signup(any(SignupRequest.class))).thenReturn(mockUser);

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
