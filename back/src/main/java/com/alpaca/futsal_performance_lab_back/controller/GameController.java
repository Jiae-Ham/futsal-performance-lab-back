package com.alpaca.futsal_performance_lab_back.controller;

import com.alpaca.futsal_performance_lab_back.dto.request.game.TeamSetupRequestDTO;
import com.alpaca.futsal_performance_lab_back.service.game.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
@Slf4j
public class GameController {

    private final GameService gameService;

    @PostMapping("/team-setup/{gameId}/{userId}")
    public void saveTeamSetup(
            @PathVariable Integer gameId,
            @PathVariable String userId,
            @RequestBody TeamSetupRequestDTO dto
    ) {
        gameService.saveTeamSetup(gameId, userId, dto);
    }
}
