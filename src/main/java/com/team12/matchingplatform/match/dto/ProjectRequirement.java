package com.team12.matchingplatform.match.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequirement {
    private String title;
    private String description;
    private List<String> requiredSkills;
    private BigDecimal budget;
}
