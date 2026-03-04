package com.team12.matchingplatform.profile.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileRequest {
    private String bio;

    @NotBlank(message = "Skills are required")
    private String skills;
    private String portfolioUrl;
    private BigDecimal hourlyRate;
}
