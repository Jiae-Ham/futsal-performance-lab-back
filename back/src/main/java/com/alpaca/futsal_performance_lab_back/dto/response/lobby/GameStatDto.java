package com.alpaca.futsal_performance_lab_back.dto.response.lobby;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 게임 통계 정보 DTO
 * 사용자의 게임별 성능 통계 정보를 담는 객체입니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameStatDto {
    
    /**
     * 게임 ID
     */
    private Long gameId;
    
    /**
     * 게임 날짜
     */
    private LocalDateTime gameDate;
    
    /**
     * 경기장 이름
     */
    private String stadiumName;
    
    /**
     * 골 수
     */
    private Integer goals;
    
    /**
     * 어시스트 수
     */
    private Integer assists;
    
    /**
     * 이동 거리 (km)
     */
    private Double distance;
    
    /**
     * 평균 속도 (km/h)
     */
    private Double avgSpeed;
    
    /**
     * 최고 속도 (km/h)
     */
    private Double maxSpeed;
    
    /**
     * 패스 성공률 (%)
     */
    private Double passAccuracy;
    
    /**
     * 슈팅 수
     */
    private Integer shots;
    
    /**
     * 유효 슈팅 수
     */
    private Integer shotsOnTarget;
}
