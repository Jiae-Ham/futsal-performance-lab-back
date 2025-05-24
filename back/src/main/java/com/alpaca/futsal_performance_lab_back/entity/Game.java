package com.alpaca.futsal_performance_lab_back.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "game")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_id")
    private Integer gameId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "ended_at", nullable = false)
    private LocalDateTime endedAt;

//    @Column(name = "password", nullable = false)
//    private String password;

    @Setter
    @Column(name = "active")
    private Integer active;


    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<SetAssign> setAssigns = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stadium_id",referencedColumnName = "stadium_id" ,nullable = false)
    private Stadium stadium;

}
