package com.alpaca.futsal_performance_lab_back.repository;

import com.alpaca.futsal_performance_lab_back.dto.response.lobby.LobbyUserInfoResponse;
import com.alpaca.futsal_performance_lab_back.entity.GameAssign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GameAssignRepository extends JpaRepository<GameAssign, Integer> {

    // 해당 게임에 특정 사용자가 참가 중인지 여부 확인
    boolean existsByGame_GameIdAndAppUser_UserId(Integer gameId, String userId);

    // 게임 내 총 참가자 수 카운트
    int countByGame_GameId(Integer gameId);

    // 해당 게임에서 사용자가 방장인지 여부 확인
    boolean existsByGame_GameIdAndAppUser_UserIdAndHostTrue(int gameId, String userId);

    @Query("SELECT new com.alpaca.futsal_performance_lab_back.dto.response.lobby.LobbyUserInfoResponse(u.userId, u.nickname) " +
            "FROM GameAssign ga JOIN ga.appUser u WHERE ga.game.gameId = :gameId")
    List<LobbyUserInfoResponse> findUserSimpleInfoByGameId(@Param("gameId") Integer gameId);

}
