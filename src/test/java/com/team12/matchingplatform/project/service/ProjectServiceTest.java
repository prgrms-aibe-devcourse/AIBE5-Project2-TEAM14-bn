package com.team12.matchingplatform.project.service;

import com.team12.matchingplatform.auth.entity.User;
import com.team12.matchingplatform.auth.entity.UserRole;
import com.team12.matchingplatform.auth.repository.UserRepository;
import com.team12.matchingplatform.project.dto.ProjectRequest;
import com.team12.matchingplatform.project.entity.Project;
import com.team12.matchingplatform.project.entity.ProjectStatus;
import com.team12.matchingplatform.project.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProjectService projectService;

    @Test
    void createProject_successfully() {
        User client = User.builder()
                .id(1L)
                .email("client@example.com")
                .name("Client")
                .role(UserRole.USER)
                .build();

        ProjectRequest request = ProjectRequest.builder()
                .title("Test Project")
                .description("Description")
                .requiredSkills("Java, Spring")
                .budget(new BigDecimal("1000.00"))
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> {
            Project p = invocation.getArgument(0);
            return Project.builder()
                    .id(1L)
                    .title(p.getTitle())
                    .description(p.getDescription())
                    .status(ProjectStatus.OPEN)
                    .client(client)
                    .build();
        });

        Project result = projectService.createProject(1L, request);
        assertThat(result.getTitle()).isEqualTo("Test Project");
        assertThat(result.getStatus()).isEqualTo(ProjectStatus.OPEN);
    }
}
