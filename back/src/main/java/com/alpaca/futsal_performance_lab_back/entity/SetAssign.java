package com.alpaca.futsal_performance_lab_back.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "set_assign")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter

public class SetAssign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "set_assign_id")
    private Integer setAssignId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Setter
    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Setter
    @Column(name = "lineup", length = 4000)
    private String lineup;

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

}