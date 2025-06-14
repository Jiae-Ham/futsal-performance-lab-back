package com.alpaca.futsal_performance_lab_back.controller;

import com.alpaca.futsal_performance_lab_back.dto.request.game.TeamSetupRequestDTO;
import com.alpaca.futsal_performance_lab_back.dto.response.game.SaveTeamSetupResponse;
import com.alpaca.futsal_performance_lab_back.dto.response.game.TagAssignResponse;
import com.alpaca.futsal_performance_lab_back.service.game.GameService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
@Slf4j
public class GameController {

    private final GameService gameService;

    @PostMapping("/team-setup/{gameId}/{userId}")
    public SaveTeamSetupResponse saveTeamSetup(
            @PathVariable Integer gameId,
            @PathVariable String userId,
            @RequestBody TeamSetupRequestDTO dto
    ) {
        return gameService.saveTeamSetup(gameId, userId, dto);
    }

    @GetMapping("/tag/{gameId}/{setAssginId}/{userId}")
    public List<TagAssignResponse> tagAssign(
            @PathVariable Integer gameId,
            @PathVariable Integer setAssginId,
            @PathVariable String userId
    ) throws JsonProcessingException {
        return gameService.tagAssign(gameId,setAssginId,userId);
    }

    @PostMapping("/start/live/{gameId}/{setAssignId}/{userId}")
    public ResponseEntity<String> gameStart(
            @PathVariable Integer gameId,
            @PathVariable Integer setAssignId,
            @PathVariable String userId
    ){
        gameService.gameStart(gameId, setAssignId, userId);
        return ResponseEntity.ok("게임 시작 완료");
    }
    @PostMapping("/stop/live/{gameId}/{setAssignId}/{userId}")
    public void gameStop(
            @PathVariable Integer gameId,
            @PathVariable Integer setAssignId,
            @PathVariable String userId
    ){
        gameService.gameStop(gameId, setAssignId, userId);
    }
    @PostMapping("/end/live/{gameId}/{userId}")
    public void gameEnd(
            @PathVariable Integer gameId,
            @PathVariable String userId
    ){
        gameService.gameEnd(gameId, userId);
    }
}
