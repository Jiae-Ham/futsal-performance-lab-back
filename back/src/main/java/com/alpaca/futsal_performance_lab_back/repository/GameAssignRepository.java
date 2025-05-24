package com.alpaca.futsal_performance_lab_back.repository;

import com.alpaca.futsal_performance_lab_back.entity.GameAssign;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameAssignRepository extends JpaRepository<GameAssign, Integer> {

    // 해당 게임에 특정 사용자가 참가 중인지 여부 확인
    boolean existsByGame_GameIdAndAppUser_UserId(Integer gameId, String userId);

    // 게임 내 총 참가자 수 카운트
    int countByGame_GameId(Integer gameId);

    // 해당 게임에서 사용자가 방장인지 여부 확인
    boolean existsByGame_GameIdAndAppUser_UserIdAndHostTrue(int gameId, String userId);
}
