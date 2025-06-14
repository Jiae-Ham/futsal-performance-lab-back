package com.alpaca.futsal_performance_lab_back.repository;

import com.alpaca.futsal_performance_lab_back.entity.Summary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSummaryRepository extends JpaRepository<Summary, Integer> {
    /**
     * 특정 사용자의 최근 7세트 Summary 조회 (SetAssign의 createdAt 기준 내림차순)
     * @param userId 사용자 ID
     * @return 최근 7세트의 Summary 리스트
     */
    @Query("SELECT s FROM Summary s " +
            "WHERE s.appUser.userId = :userId " +
            "ORDER BY s.setAssign.createdAt DESC " +
            "LIMIT 7")
    List<Summary> findTop7ByAppUserUserIdOrderBySetAssignCreatedAtDesc(@Param("userId") String userId);
}
