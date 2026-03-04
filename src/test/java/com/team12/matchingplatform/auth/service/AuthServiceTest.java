package com.team12.matchingplatform.auth.service;

import com.team12.matchingplatform.auth.dto.SignupRequest;
import com.team12.matchingplatform.auth.entity.User;
import com.team12.matchingplatform.auth.entity.UserRole;
import com.team12.matchingplatform.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void signup_createsUser_successfully() {
        SignupRequest request = SignupRequest.builder()
                .email("test@example.com")
                .password("password123")
                .name("Test User")
                .role(UserRole.USER)
                .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u = User.builder()
                    .id(1L)
                    .email(u.getEmail())
                    .password(u.getPassword())
                    .name(u.getName())
                    .role(u.getRole())
                    .build();
            return u;
        });

        User result = authService.signup(request);
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getRole()).isEqualTo(UserRole.USER);
    }
}
