package com.alpaca.futsal_performance_lab_back.repository;

import com.alpaca.futsal_performance_lab_back.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Integer> {

    // 특정 경기장에서 isActive = 1인 게임(아직 시작하지 않은 방) 반환
    Optional<Game> findFirstByActiveAndStadium_StadiumId(int isActive, int stadiumId);
}
