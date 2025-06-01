package com.alpaca.futsal_performance_lab_back.dto.response.lobby;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 사용자 성능 통계 요약 응답 DTO
 * 사용자의 전체 성능 통계 요약 정보를 담는 응답 객체입니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceSummaryResponseDto {
    
    /**
     * 사용자 개인 통계 정보
     */
    private PlayerStatDto playerStat;
    
    /**
     * 사용자 게임별 통계 정보 목록
     */
    private List<GameStatDto> gameStats;
}
