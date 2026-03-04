package com.team12.matchingplatform.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team12.matchingplatform.auth.service.AuthService;
import com.team12.matchingplatform.config.AppConfig;
import com.team12.matchingplatform.config.SecurityConfig;
import com.team12.matchingplatform.project.dto.ProjectRequest;
import com.team12.matchingplatform.project.entity.Project;
import com.team12.matchingplatform.project.entity.ProjectStatus;
import com.team12.matchingplatform.project.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
@Import({SecurityConfig.class, AppConfig.class})
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProjectService projectService;

    @MockBean
    private AuthService authService;

    @Test
    @WithMockUser
    void createProject_returnsOk() throws Exception {
        ProjectRequest request = ProjectRequest.builder()
                .title("Test Project")
                .description("Description")
                .requiredSkills("Java, Spring")
                .budget(new BigDecimal("1000.00"))
                .build();

        Project mockProject = Project.builder()
                .id(1L)
                .title("Test Project")
                .status(ProjectStatus.OPEN)
                .build();

        when(projectService.createProject(anyLong(), any(ProjectRequest.class))).thenReturn(mockProject);

        mockMvc.perform(post("/api/projects")
                .param("userId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getProject_returnsOk() throws Exception {
        Project mockProject = Project.builder()
                .id(1L)
                .title("Test Project")
                .status(ProjectStatus.OPEN)
                .build();

        when(projectService.getProject(1L)).thenReturn(mockProject);

        mockMvc.perform(get("/api/projects/1"))
                .andExpect(status().isOk());
    }
}
