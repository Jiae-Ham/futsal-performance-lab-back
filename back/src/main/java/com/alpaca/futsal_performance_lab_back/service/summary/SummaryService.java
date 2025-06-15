package com.alpaca.futsal_performance_lab_back.service.summary;

import com.alpaca.futsal_performance_lab_back.dto.response.summary.SummaryResponse;
import com.alpaca.futsal_performance_lab_back.entity.Summary;
import com.alpaca.futsal_performance_lab_back.repository.SummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SummaryService {

    private final SummaryRepository summaryRepo;

    /**
     *  게임-ID, 유저-ID 로 세트별 Summary 목록 반환
     */
    public List<SummaryResponse> getSummaries(Integer gameId, String userId) {

        return summaryRepo
                .findByGame_GameIdAndAppUser_UserIdOrderBySetAssign_CreatedAtAsc(gameId, userId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private SummaryResponse toDto(Summary s) {
        return new SummaryResponse(
                s.getSetAssign().getSetAssignId(),
                s.getAggressionScore(),
                s.getAgilityScore(),
                s.getAttackScore(),
                s.getCaloriesBurnedKcal(),
                s.getDefenseScore(),
                s.getGameScore(),
                s.getPlayTimeMinutes(),
                s.getSpeedScore(),
                s.getSprintCount(),
                s.getStaminaScore()

        );
    }
}

