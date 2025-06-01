package com.alpaca.futsal_performance_lab_back.controller;

import com.alpaca.futsal_performance_lab_back.dto.response.lobby.GameJoinResponse;
import com.alpaca.futsal_performance_lab_back.dto.response.lobby.LobbyIsReadyResponse;
import com.alpaca.futsal_performance_lab_back.dto.response.lobby.LobbyStatusResponse;
import com.alpaca.futsal_performance_lab_back.service.lobby.LobbyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lobby")
@RequiredArgsConstructor
public class LobbyController {

    private final LobbyService lobbyService;

    /**
     * 참가 신청: 프론트에서 userId를 보내면 새 gameId를 생성 후 DTO로 반환
     */
    @PostMapping("/nearest/{userId}")
    public ResponseEntity<GameJoinResponse> joinNearestLobby(@PathVariable String userId) {
        int stadiumId = 1; // BLE 비콘 기반으로 받아온 값이라 가정

        int gameId = lobbyService.createGameForUser(userId, stadiumId);
        return ResponseEntity.ok(new GameJoinResponse(gameId));
    }

    /**
     * 방 상태 확인: 현재 인원수와 isActive 상태를 DTO로 반환
     */
    @GetMapping("/status/{gameId}/{userId}")
    public ResponseEntity<LobbyStatusResponse> getLobbyStatus(
            @PathVariable int gameId,
            @PathVariable String userId
    ) {
        LobbyStatusResponse status = lobbyService.getLobbyStatus(gameId, userId);
        return ResponseEntity.ok(status);
    }

    /**
     * 준비 완료: isActive 상태를 2로 업데이트하고 결과 DTO 반환
     */
    @PostMapping("/ready/{gameId}/{userId}")
    public ResponseEntity<LobbyIsReadyResponse> readyLobby(
            @PathVariable int gameId,
            @PathVariable String userId
    ) {
        lobbyService.markReady(gameId, userId);
        LobbyIsReadyResponse response = new LobbyIsReadyResponse(true);
        return ResponseEntity.ok(response);
    }
}



