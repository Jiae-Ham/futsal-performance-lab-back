package com.alpaca.futsal_performance_lab_back.controller;

import com.alpaca.futsal_performance_lab_back.dto.request.game.TeamSetupRequestDTO;
import com.alpaca.futsal_performance_lab_back.entity.Game;
import com.alpaca.futsal_performance_lab_back.entity.SetAssign;
import com.alpaca.futsal_performance_lab_back.repository.GameRepository;
import com.alpaca.futsal_performance_lab_back.repository.SetAssignRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
@Slf4j
public class GameController {
    private final GameRepository gameRepository;
    private final SetAssignRepository setAssignRepository;
    @PostMapping("/team-setup/{gameId}/{userId}")
    public ResponseEntity<?> saveTeamSetup(
            @PathVariable Integer gameId,
            @PathVariable String userId,
            @RequestBody TeamSetupRequestDTO dto
    ) {
        log.info("팀 세팅 요청 - gameId: {}, userId: {}, timestamp: {}", gameId, userId, dto.getTimestamp());

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 경기 ID입니다."));

        // ① 요청 JSON을 문자열로 직렬화
        String lineupJson = "";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            lineupJson = objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            log.error("라인업 직렬화 실패", e);
            return ResponseEntity.internalServerError().body("라인업 직렬화 오류");
        }

        // ② SetAssign 생성 및 저장
        SetAssign setAssign = SetAssign.builder()
                .createdAt(LocalDateTime.now())
                .startedAt(LocalDateTime.parse(dto.getTimestamp())) // ← 필요 시 파싱
                .lineup(lineupJson)
                .game(game)
                .build();

        setAssignRepository.save(setAssign);
        log.info("라인업 저장 완료: {}", setAssign.getSetAssignId());

        return ResponseEntity.ok().body("팀 세팅 저장 완료");
    }

}
