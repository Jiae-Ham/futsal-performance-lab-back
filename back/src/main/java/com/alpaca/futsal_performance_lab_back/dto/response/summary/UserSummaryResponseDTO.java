package com.alpaca.futsal_performance_lab_back.dto.response.summary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSummaryResponseDTO {
    // 공격 점수
    private double attackScore;
    
    //속도 점수
    private double speedScore;
    
    // 적극성
    private double aggressionScore;
    
    // 민첩성
    private double agilityScore;
    
    // 수비
    private double defenseScore;
    
    //체력
    private double staminaScore;

    // 집계에 사용된 세트수
    private int totalSets;
}
