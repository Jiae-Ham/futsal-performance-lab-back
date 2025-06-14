package com.alpaca.futsal_performance_lab_back.service.summary;

import com.alpaca.futsal_performance_lab_back.dto.response.summary.UserSummaryResponseDTO;
import com.alpaca.futsal_performance_lab_back.entity.Summary;
import com.alpaca.futsal_performance_lab_back.repository.UserSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserStatsServiceImpl implements UserStatsService {

    private final UserSummaryRepository userSummaryRepository;

    @Override
    public UserSummaryResponseDTO getRecentSevenSetsStats(String userId) {
        List<Summary> recentSummaries = userSummaryRepository.findTop7ByAppUserUserIdOrderBySetAssignCreatedAtDesc(userId);

        if (recentSummaries.isEmpty()) {
            // 데이터가 없는 경우 0으로 초기화된 DTO 반환
            return UserSummaryResponseDTO.builder()
                    .attackScore(0.0)
                    .speedScore(0.0)
                    .aggressionScore(0.0)
                    .agilityScore(0.0)
                    .defenseScore(0.0)
                    .staminaScore(0.0)
                    .totalSets(0)
                    .build();
        }
        // 각 능력치 점수의 평균 계산
        double attackAvg = recentSummaries.stream()
                .mapToDouble(Summary::getAttackScore)
                .average()
                .orElse(0.0);

        double speedAvg = recentSummaries.stream()
                .mapToDouble(Summary::getSpeedScore)
                .average()
                .orElse(0.0);

        double aggressionAvg = recentSummaries.stream()
                .mapToDouble(Summary::getAggressionScore)
                .average()
                .orElse(0.0);

        double agilityAvg = recentSummaries.stream()
                .mapToDouble(Summary::getAgilityScore)
                .average()
                .orElse(0.0);

        double defenseAvg = recentSummaries.stream()
                .mapToDouble(Summary::getDefenseScore)
                .average()
                .orElse(0.0);

        double staminaAvg = recentSummaries.stream()
                .mapToDouble(Summary::getStaminaScore)
                .average()
                .orElse(0.0);

        return UserSummaryResponseDTO.builder()
                .attackScore(Math.round(attackAvg * 100.0) / 100.0)    // 소수점 둘째자리까지
                .speedScore(Math.round(speedAvg * 100.0) / 100.0)
                .aggressionScore(Math.round(aggressionAvg * 100.0) / 100.0)
                .agilityScore(Math.round(agilityAvg * 100.0) / 100.0)
                .defenseScore(Math.round(defenseAvg * 100.0) / 100.0)
                .staminaScore(Math.round(staminaAvg * 100.0) / 100.0)
                .totalSets(recentSummaries.size())
                .build();
    }
}
