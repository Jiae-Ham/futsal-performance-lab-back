package com.alpaca.futsal_performance_lab_back.repository;

import com.alpaca.futsal_performance_lab_back.entity.GamePerformance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GamePerformanceRepository extends JpaRepository<GamePerformance, Long> {

    @Query("SELECT gp FROM GamePerformance gp WHERE gp.gameId = :gameId AND gp.userId = :userId")
    Optional<GamePerformance> findByGameIdAndUserId(@Param("gameId") Integer gameId, @Param("userId") String userId);
}