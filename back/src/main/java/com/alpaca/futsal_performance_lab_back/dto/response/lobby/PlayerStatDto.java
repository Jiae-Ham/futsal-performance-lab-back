package com.alpaca.futsal_performance_lab_back.dto.response.lobby;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 선수 통계 정보 DTO
 * 사용자의 개인 성능 통계 정보를 담는 객체입니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerStatDto {
    
    /**
     * 사용자 ID
     */
    private Long userId;
    
    /**
     * 사용자 이름
     */
    private String userName;
    
    /**
     * 총 경기 수
     */
    private Integer totalGames;
    
    /**
     * 총 골 수
     */
    private Integer totalGoals;
    
    /**
     * 총 어시스트 수
     */
    private Integer totalAssists;
    
    /**
     * 평균 이동 거리 (km)
     */
    private Double avgDistance;
    
    /**
     * 평균 속도 (km/h)
     */
    private Double avgSpeed;
    
    /**
     * 최고 속도 (km/h)
     */
    private Double maxSpeed;
    
    /**
     * 평균 패스 성공률 (%)
     */
    private Double avgPassAccuracy;
    
    /**
     * 마지막 경기 날짜
     */
    private LocalDateTime lastGameDate;
}
