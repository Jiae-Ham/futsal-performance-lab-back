package com.alpaca.futsal_performance_lab_back.service.summary;

import com.alpaca.futsal_performance_lab_back.dto.response.summary.UserSummaryNormalizedResponseDTO;

public interface UserStatsNormalizedService {
    UserSummaryNormalizedResponseDTO getRecentSevenSetsStats(String userId);
}
