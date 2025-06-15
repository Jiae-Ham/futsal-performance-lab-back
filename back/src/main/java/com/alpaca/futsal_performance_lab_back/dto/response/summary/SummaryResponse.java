package com.alpaca.futsal_performance_lab_back.dto.response.summary;

public record SummaryResponse(
        Integer setAssignId,
        double aggressionScore,
        double agilityScore,
        double attackScore,
        double caloriesBurnedKcal,
        double defenseScore,
        int gameScore,
        double playTimeMinutes,
        double speedScore,
        int sprintCount,
        double staminaScore
) {}
