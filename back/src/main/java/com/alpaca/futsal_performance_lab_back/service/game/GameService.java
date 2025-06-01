package com.alpaca.futsal_performance_lab_back.service.game;

import com.alpaca.futsal_performance_lab_back.dto.request.game.TeamSetupRequestDTO;
import com.alpaca.futsal_performance_lab_back.entity.Game;
import com.alpaca.futsal_performance_lab_back.entity.SetAssign;
import com.alpaca.futsal_performance_lab_back.exception.game.GameAccessException;
import com.alpaca.futsal_performance_lab_back.exception.game.GameLineupSerializationException;
import com.alpaca.futsal_performance_lab_back.repository.GameRepository;
import com.alpaca.futsal_performance_lab_back.repository.SetAssignRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {

    private final GameRepository gameRepository;
    private final SetAssignRepository setAssignRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void saveTeamSetup(Integer gameId, String userId, TeamSetupRequestDTO dto) {
        log.info("팀 세팅 요청 - gameId: {}, userId: {}, timestamp: {}", gameId, userId, dto.getTimestamp());

        Game game = gameRepository.findById(gameId)
                .orElseThrow(GameAccessException::gameNotFound);

        String lineupJson;
        try {
            lineupJson = objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            log.error("라인업 직렬화 실패", e);
            throw GameLineupSerializationException.from(e);
        }

        SetAssign setAssign = SetAssign.builder()
                .createdAt(LocalDateTime.now())
                .startedAt(LocalDateTime.parse(dto.getTimestamp()))
                .lineup(lineupJson)
                .game(game)
                .build();

        setAssignRepository.save(setAssign);
        log.info("라인업 저장 완료: setAssignId={}", setAssign.getSetAssignId());
    }
}
