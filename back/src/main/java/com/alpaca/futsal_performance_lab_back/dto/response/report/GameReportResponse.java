package com.alpaca.futsal_performance_lab_back.dto.response.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameReportResponse {
    private Integer gameId;
    private String userId;
    private String playerName;
    private String gameDate;
    private Integer totalPlayTime; // 분 단위

    // 5개 핵심 지표
    private Double avgSpeed;           // 평균 속도 (km/h)
    private Integer totalDistance;     // 총 이동거리 (m)
    private Integer sprintCount;       // 스프린트 횟수
    private Double passAccuracy;       // 패스 성공률 (%)
    private Double shotAccuracy;       // 슛 성공률 (%)

    private String gameResult;         // WIN, LOSE, DRAW
    // private Integer finalScore;        // 최종 스코어
}