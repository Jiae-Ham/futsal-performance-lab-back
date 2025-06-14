package com.alpaca.futsal_performance_lab_back.service.summary;

import com.alpaca.futsal_performance_lab_back.dto.response.summary.UserSummaryResponseDTO;

public interface UserStatsService {
    UserSummaryResponseDTO getRecentSevenSetsStats(String userId);
}
