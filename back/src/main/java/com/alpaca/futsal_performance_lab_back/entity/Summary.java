package com.alpaca.futsal_performance_lab_back.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "summary")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter    // 필드 변경을 위해 Setter 추가
public class Summary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "summary_id")
    private Integer summaryId;

    /** 총 출전 시간 (분 단위) */
    @Column(name = "play_time_minutes", nullable = false)
    private double playTimeMinutes;

    /** 스프린트 횟수 */
    @Column(name = "sprint_count", nullable = false)
    private int sprintCount;

    /** 소모 칼로리 (kcal) */
    @Column(name = "calories_burned_kcal", nullable = false)
    private double caloriesBurnedKcal;

    /** 경기 스코어 (팀 기록 혹은 개인 기여도 점수 등) */
    @Column(name = "game_score", nullable = false)
    private int gameScore;

    /** 공격 점수 (0~10 스케일) */
    @Column(name = "attack_score", nullable = false)
    private double attackScore;

    /** 속도 점수 (0~10 스케일) */
    @Column(name = "speed_score", nullable = false)
    private double speedScore;

    /** 적극성 점수 (0~10 스케일) */
    @Column(name = "aggression_score", nullable = false)
    private double aggressionScore;

    /** 민첩성 점수 (0~10 스케일) */
    @Column(name = "agility_score", nullable = false)
    private double agilityScore;

    /** 수비 점수 (0~10 스케일) */
    @Column(name = "defense_score", nullable = false)
    private double defenseScore;

    /** 체력 점수 (0~10 스케일) */
    @Column(name = "stamina_score", nullable = false)
    private double staminaScore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private AppUser appUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", referencedColumnName = "game_id", nullable = false)
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "set_assign_id", referencedColumnName = "set_assign_id", nullable = false)
    private SetAssign setAssign;

}
