package com.alpaca.futsal_performance_lab_back.repository;

import com.alpaca.futsal_performance_lab_back.entity.Summary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SummaryRepository extends JpaRepository<Summary, Integer> {
    /** 게임 + 유저에 해당하는 세트별 Summary 전체 반환 (최신순) */
    List<Summary> findByGame_GameIdAndAppUser_UserIdOrderBySetAssign_CreatedAtAsc(
            Integer gameId, String userId);
}
