package com.alpaca.futsal_performance_lab_back.service.summary;

import com.alpaca.futsal_performance_lab_back.dto.response.summary.UserSummaryNormalizedResponseDTO;
import com.alpaca.futsal_performance_lab_back.entity.Summary;
import com.alpaca.futsal_performance_lab_back.repository.UserSummaryRepository;
import com.alpaca.futsal_performance_lab_back.service.utils.ScoreNormalizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserStatsNormalizedServiceImpl implements UserStatsNormalizedService {

    private final UserSummaryRepository repo;
    private final ScoreNormalizer norm;

    @Override
    public UserSummaryNormalizedResponseDTO getRecentSevenSetsStats(String userId) {

        var summaries = repo.findTop7ByAppUserUserIdOrderBySetAssignCreatedAtDesc(userId);

        int setCnt = summaries.size();
        if (setCnt == 0) {
            return new UserSummaryNormalizedResponseDTO(0,0,0,0,0,0,0);
        }

        /* 1) raw 평균 */
        double atkAvg = summaries.stream().mapToDouble(Summary::getAttackScore   ).average().orElse(0);
        double spdAvg = summaries.stream().mapToDouble(Summary::getSpeedScore    ).average().orElse(0);
        double agrAvg = summaries.stream().mapToDouble(Summary::getAggressionScore).average().orElse(0);
        double agiAvg = summaries.stream().mapToDouble(Summary::getAgilityScore  ).average().orElse(0);
        double defAvg = summaries.stream().mapToDouble(Summary::getDefenseScore  ).average().orElse(0);
        double staAvg = summaries.stream().mapToDouble(Summary::getStaminaScore  ).average().orElse(0);

        /* 2) 0~10 스케일 선형 변환 (예시 min·max) */
        double atkN = norm.toTenScale(atkAvg, 0,  8);     // 평균 속도 0~8 m/s
        double spdN = norm.toTenScale(spdAvg, 0, 40);     // 최고 속도 0~40 km/h
        double agrN = norm.toTenScale(agrAvg, 0, 15);     // 스프린트 0~15 회
        double agiN = norm.toTenScale(agiAvg, 0,120);     // COD 0~120 회
        double defN = norm.toTenScale(defAvg, 0,100);     // 커버리지 0~100 점
        double staN = norm.toTenScale(staAvg, 0, 10);     // 이동 거리 0~10 km


        return new UserSummaryNormalizedResponseDTO(
                norm.round2(atkN),
                norm.round2(spdN),
                norm.round2(agrN),
                norm.round2(agiN),
                norm.round2(defN),
                norm.round2(staN),
                setCnt
        );
    }
}
