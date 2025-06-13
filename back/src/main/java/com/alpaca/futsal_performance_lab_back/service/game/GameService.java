package com.alpaca.futsal_performance_lab_back.service.game;

import com.alpaca.futsal_performance_lab_back.dto.request.game.TeamSetupRequestDTO;
import com.alpaca.futsal_performance_lab_back.dto.response.game.SaveTeamSetupResponse;
import com.alpaca.futsal_performance_lab_back.entity.Game;
import com.alpaca.futsal_performance_lab_back.entity.SetAssign;
import com.alpaca.futsal_performance_lab_back.exception.game.GameAccessException;
import com.alpaca.futsal_performance_lab_back.exception.game.GameLineupSerializationException;
import com.alpaca.futsal_performance_lab_back.exception.lobby.GameNotFoundException;
import com.alpaca.futsal_performance_lab_back.repository.GameAssignRepository;
import com.alpaca.futsal_performance_lab_back.repository.GameRepository;
import com.alpaca.futsal_performance_lab_back.repository.SetAssignRepository;
import com.alpaca.futsal_performance_lab_back.service.utils.ValidateHost;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {

    private final GameRepository gameRepository;
    private final GameAssignRepository gameAssignRepository;
    private final SetAssignRepository setAssignRepository;
    private final ValidateHost validateHost;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final WebClient webClient = WebClient.builder().baseUrl("http://localhost:8081").build();


    public SaveTeamSetupResponse saveTeamSetup(Integer gameId, String userId, TeamSetupRequestDTO dto) {
        validateHost.requireHostRole(gameId, userId);

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
        return new SaveTeamSetupResponse(gameId,setAssign.getSetAssignId());
    }

    public void gameStart(int gameId, int setAssignId, String userId) {
        try {
            validateHost.requireHostRole(gameId, userId);  // ✅ 방장 권한 확인

            Game game = gameRepository.findById(gameId)
                    .orElseThrow(GameNotFoundException::new);

            // isActive를 3으로 설정 (세트 진행 중)
            game.setActive(3);
            gameRepository.save(game);

            log.info("게임 시작 - gameId: {}, setAssignId: {}, userId: {}", gameId, setAssignId, userId);

            // 외부 시스템에 게임 시작 알림
            webClient.post()
                    .uri("/api/game/live/start")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of(
                            "gameId", gameId,
                            "setAssignId", setAssignId
                    ))
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

        } catch (Exception e) {
            log.error("게임 시작 실패 - gameId: {}, error: {}", gameId, e.getMessage());
            throw e;
        }
    }

    public void gameStop(int gameId, int setAssignId, String userId) {
        validateHost.requireHostRole(gameId, userId);

        Game game = gameRepository.findById(gameId)
                .orElseThrow(GameNotFoundException::new);

        SetAssign setAssign = setAssignRepository.findById(setAssignId)
                .orElseThrow(() -> new IllegalArgumentException("세트 없음"));

        setAssign.setEndedAt(LocalDateTime.now());
        setAssignRepository.save(setAssign);

        game.setActive(4);
        gameRepository.save(game);

        webClient.post()
                .uri("/api/game/live/stop")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("gameId", gameId))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public void gameEnd(int gameId, String userId) {
        validateHost.requireHostRole(gameId, userId);

        Game game = gameRepository.findById(gameId)
                .orElseThrow(GameNotFoundException::new);

        game.setEndedAt(LocalDateTime.now());
        game.setActive(5);
        gameRepository.save(game);
    }
}
