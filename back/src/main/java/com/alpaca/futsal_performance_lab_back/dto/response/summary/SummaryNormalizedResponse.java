package com.alpaca.futsal_performance_lab_back.dto.response.summary;

public record SummaryNormalizedResponse(
        Integer setAssignId,
        /* 정규화 점수 0~10 */
        double normAggression,
        double normAgility,
        double normAttack,
        double normDefense,
        double normSpeed,
        double normStamina,

        /* 그대로 노출 */
        int    rawGameScore,
        double rawPlayTimeMinutes,
        int    rawSprintCount,
        double rawCaloriesBurnedKcal
) {}
