package com.alpaca.futsal_performance_lab_back.repository;

import com.alpaca.futsal_performance_lab_back.entity.Summary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SummaryRepository extends JpaRepository<Summary, Integer> {
    Optional<Summary> findByGame_GameIdAndAppUser_UserId(Integer gameId, String userId);
}
