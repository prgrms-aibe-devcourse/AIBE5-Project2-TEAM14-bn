package com.team12.matchingplatform.match.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchResult {
    private Long freelancerId;
    private String freelancerName;
    private double matchScore;
    private String reason;
}
