package com.alpaca.futsal_performance_lab_back.dto.response.summary;

public record UserSummaryNormalizedResponseDTO(
        double attack,
        double speed,
        double aggression,
        double agility,
        double defense,
        double stamina,
        int    totalSets
) {}
