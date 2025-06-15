package com.alpaca.futsal_performance_lab_back.service.game;

import com.alpaca.futsal_performance_lab_back.dto.Internal.Lineup;
import com.alpaca.futsal_performance_lab_back.dto.Internal.LineupWithTags;
import com.alpaca.futsal_performance_lab_back.dto.Internal.PlayerInfo;
import com.alpaca.futsal_performance_lab_back.dto.request.game.TeamSetupRequestDTO;
import com.alpaca.futsal_performance_lab_back.dto.response.game.SaveTeamSetupResponse;
import com.alpaca.futsal_performance_lab_back.dto.response.game.TagAssignResponse;
import com.alpaca.futsal_performance_lab_back.entity.Game;
import com.alpaca.futsal_performance_lab_back.entity.SetAssign;
import com.alpaca.futsal_performance_lab_back.entity.Stadium;
import com.alpaca.futsal_performance_lab_back.entity.Tag;
import com.alpaca.futsal_performance_lab_back.exception.game.GameAccessException;
import com.alpaca.futsal_performance_lab_back.exception.game.GameLineupSerializationException;
import com.alpaca.futsal_performance_lab_back.exception.lobby.GameNotFoundException;
import com.alpaca.futsal_performance_lab_back.repository.GameAssignRepository;
import com.alpaca.futsal_performance_lab_back.repository.GameRepository;
import com.alpaca.futsal_performance_lab_back.repository.SetAssignRepository;
import com.alpaca.futsal_performance_lab_back.repository.TagRepository;
import com.alpaca.futsal_performance_lab_back.service.utils.ValidateHost;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {

    private final GameRepository gameRepository;
    private final TagRepository tagRepository;
    private final SetAssignRepository setAssignRepository;
    private final ValidateHost validateHost;
    private final GamePostProcessor gamePostProcessor;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final WebClient webClient = WebClient.builder().baseUrl("http://100.80.73.116:8082").build();


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

    public List<TagAssignResponse> tagAssign(Integer gameId, Integer setAssignId, String userId) throws JsonProcessingException {
        validateHost.requireHostRole(gameId, userId);  // ✅ 방장 권한 확인

        // 게임 정보 조회
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("게임을 찾을 수 없습니다: " + gameId));

        // 해당 게임의 스타디움 ID 조회
        Integer stadiumId = game.getStadium().getStadiumId();

        // 해당 스타디움에서 아직 할당되지 않은 태그 목록 조회
        List<Tag> tags = tagRepository.findByStadium_StadiumIdAndAssignedFalse(stadiumId);

        // 세트 정보 조회
        SetAssign setAssign = setAssignRepository.findById(setAssignId)
                .orElseThrow(() -> new IllegalArgumentException("세트를 찾을 수 없습니다."));

        // lineup JSON 문자열 → Lineup 객체로 역직렬화
        String lineupJson = setAssign.getLineup();
        Lineup lineup = objectMapper.readValue(lineupJson, Lineup.class);

        // 라인업에 포함된 전체 유저 ID 수집
        List<String> userIds = new ArrayList<>();
        userIds.addAll(lineup.getRedTeam().values());
        userIds.addAll(lineup.getBlueTeam().values());

        // 유저 수보다 태그 수가 적으면 예외 발생
        if (userIds.size() > tags.size()) {
            throw new IllegalStateException("사용자 수보다 사용 가능한 태그가 부족합니다.");
        }

        // 최종 반환할 결과 리스트
        List<TagAssignResponse> responseList = new ArrayList<>();

        // 새로 구성할 라인업 (태그 포함)
        Map<String, PlayerInfo> redTeamTagged = new LinkedHashMap<>();
        Map<String, PlayerInfo> blueTeamTagged = new LinkedHashMap<>();

        int tagIndex = 0;

        // 레드팀 각 포지션에 태그 할당
        for (Map.Entry<String, String> entry : lineup.getRedTeam().entrySet()) {
            String position = entry.getKey();
            String uid = entry.getValue();

            Tag tag = tags.get(tagIndex++); // 태그 하나 할당
            tag.setAssigned(true);          // 할당 표시
            tagRepository.save(tag);        // DB 반영

            // 새로운 라인업에 추가 (userId + tagId)
            redTeamTagged.put(position, new PlayerInfo(uid, tag.getTagId()));

            // 응답 리스트에도 추가
            responseList.add(new TagAssignResponse(uid, tag.getTagId()));
        }

        // 블루팀도 동일하게 처리
        for (Map.Entry<String, String> entry : lineup.getBlueTeam().entrySet()) {
            String position = entry.getKey();
            String uid = entry.getValue();

            Tag tag = tags.get(tagIndex++);
            tag.setAssigned(true);
            tagRepository.save(tag);

            blueTeamTagged.put(position, new PlayerInfo(uid, tag.getTagId()));
            responseList.add(new TagAssignResponse(uid, tag.getTagId()));
        }

        // 태그가 포함된 새 라인업 구성
        LineupWithTags lineupWithTags = new LineupWithTags();
        lineupWithTags.setRedTeamFormation(lineup.getRedTeamFormation());
        lineupWithTags.setBlueTeamFormation(lineup.getBlueTeamFormation());
        lineupWithTags.setTimestamp(lineup.getTimestamp());
        lineupWithTags.setRedTeam(redTeamTagged);
        lineupWithTags.setBlueTeam(blueTeamTagged);

        // 새 lineup을 JSON 문자열로 변환 후 setAssign에 저장
        String updatedJson = objectMapper.writeValueAsString(lineupWithTags);
        setAssign.setLineup(updatedJson);
        setAssignRepository.save(setAssign); // DB 저장

        return responseList; // 프론트에 userId, tagId 쌍 반환
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

        game.setActive(4); // 경기 상태 4 = 종료
        game.setEndedAt(LocalDateTime.now());
        gameRepository.save(game);

        // Stadium에 연결된 모든 Tag의 assigned = false
        Stadium stadium = game.getStadium();
        for (Tag tag : stadium.getTags()) {
            tag.setAssigned(false);
        }

        // tagRepository가 존재한다고 가정하고 saveAll 수행
        tagRepository.saveAll(stadium.getTags());

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

        Stadium stadium = game.getStadium();
        for (Tag tag : stadium.getTags()) {
            tag.setAssigned(false);
        }
        tagRepository.saveAll(stadium.getTags());

        //ClickHouse 쿼리 백그라운드 처리
        gamePostProcessor.processAfterGameEnd(gameId);
    }

}
