package com.alpaca.futsal_performance_lab_back.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "game_performance")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GamePerformance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "game_id", nullable = false)
    private Integer gameId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "total_play_time")
    private Integer totalPlayTime; // 분 단위

    @Column(name = "avg_speed")
    private Double avgSpeed; // km/h

    @Column(name = "total_distance")
    private Integer totalDistance; // m

    @Column(name = "sprint_count")
    private Integer sprintCount;

    @Column(name = "pass_accuracy")
    private Double passAccuracy; // %

    @Column(name = "shot_accuracy")
    private Double shotAccuracy; // %
}
