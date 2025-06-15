package com.alpaca.futsal_performance_lab_back.service.summary;

import com.alpaca.futsal_performance_lab_back.dto.response.summary.SummaryResponse;
import com.alpaca.futsal_performance_lab_back.entity.Summary;
import com.alpaca.futsal_performance_lab_back.repository.SummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SummaryService {

    private final SummaryRepository summaryRepository;

    public Optional<SummaryResponse> getSummary(Integer gameId, String userId) {
        return summaryRepository
                .findByGame_GameIdAndAppUser_UserId(gameId, userId)
                .map(this::toDto);
    }

    private SummaryResponse toDto(Summary summary) {
        return new SummaryResponse(
                summary.getAggressionScore(),
                summary.getAgilityScore(),
                summary.getAttackScore(),
                summary.getCaloriesBurnedKcal(),
                summary.getDefenseScore(),
                summary.getGameScore(),
                summary.getPlayTimeMinutes(),
                summary.getSpeedScore(),
                summary.getSprintCount(),
                summary.getStaminaScore()
        );
    }
}
