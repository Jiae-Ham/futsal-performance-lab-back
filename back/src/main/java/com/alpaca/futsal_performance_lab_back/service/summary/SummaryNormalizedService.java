package com.alpaca.futsal_performance_lab_back.service.summary;

import com.alpaca.futsal_performance_lab_back.dto.response.summary.SummaryNormalizedResponse;
import com.alpaca.futsal_performance_lab_back.entity.Summary;
import com.alpaca.futsal_performance_lab_back.repository.SummaryRepository;
import com.alpaca.futsal_performance_lab_back.service.utils.ScoreNormalizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SummaryNormalizedService {

    private final SummaryRepository summaryRepo;
    private final ScoreNormalizer   norm;

    /** 게임-ID, 유저-ID 에 대한 세트별 정규화 Summary 목록 반환 */
    public List<SummaryNormalizedResponse> getSummaries(Integer gameId, String userId) {

        return summaryRepo
                .findByGame_GameIdAndAppUser_UserIdOrderBySetAssign_CreatedAtAsc(gameId, userId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private SummaryNormalizedResponse toDto(Summary s) {

        /* 예시 min·max – 실제 기준에 맞게 조정 */
        double atk = norm.round2(norm.toTenScale(s.getAttackScore(),     0,  8));
        double spd = norm.round2(norm.toTenScale(s.getSpeedScore(),      0, 40));
        double agr = norm.round2(norm.toTenScale(s.getAggressionScore(), 0, 15));
        double agi = norm.round2(norm.toTenScale(s.getAgilityScore(),    0,120));
        double def = norm.round2(norm.toTenScale(s.getDefenseScore(),    0,100)); // 필요 시
        double sta = norm.round2(norm.toTenScale(s.getStaminaScore(),    0, 10));

        return new SummaryNormalizedResponse(
                s.getSetAssign().getSetAssignId(),  // ← 세트 번호
                agr, agi, atk, def, spd, sta,
                s.getGameScore(),
                s.getPlayTimeMinutes(),
                s.getSprintCount(),
                s.getCaloriesBurnedKcal()
        );
    }
}

