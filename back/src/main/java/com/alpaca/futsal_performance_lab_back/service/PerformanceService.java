package com.alpaca.futsal_performance_lab_back.service;

import com.alpaca.futsal_performance_lab_back.dto.response.lobby.GameStatDto;
import com.alpaca.futsal_performance_lab_back.dto.response.lobby.PerformanceSummaryResponseDto;
import com.alpaca.futsal_performance_lab_back.dto.response.lobby.PlayerStatDto;
import com.alpaca.futsal_performance_lab_back.repository.PerformanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 사용자 성능 통계 요약 서비스
 * 사용자의 성능 통계 데이터를 처리하고 요약하는 비즈니스 로직을 담당합니다.
 */
@Service
public class PerformanceService {

    private final PerformanceRepository performanceRepository;

    @Autowired
    public PerformanceService(PerformanceRepository performanceRepository) {
        this.performanceRepository = performanceRepository;
    }

    /**
     * 사용자 성능 통계 요약 정보 조회
     * 
     * @param userId 사용자 ID
     * @return 사용자 성능 통계 요약 정보
     */
    public PerformanceSummaryResponseDto getPerformanceSummary(Long userId) {
        // 사용자 기본 통계 정보 조회
        PlayerStatDto playerStat = performanceRepository.getPlayerStatByUserId(userId);
        
        // 사용자 게임 통계 정보 조회
        List<GameStatDto> gameStats = performanceRepository.getGameStatsByUserId(userId);
        
        // 응답 DTO 생성 및 반환
        return PerformanceSummaryResponseDto.builder()
                .playerStat(playerStat)
                .gameStats(gameStats)
                .build();
    }
}
