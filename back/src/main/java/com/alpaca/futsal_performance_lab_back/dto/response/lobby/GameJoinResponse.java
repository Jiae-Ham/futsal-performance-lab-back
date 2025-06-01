package com.alpaca.futsal_performance_lab_back.dto.response.lobby;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.atomic.AtomicBoolean;

@Data
@AllArgsConstructor
public class GameJoinResponse {
    private int gameId;
    private AtomicBoolean isNewGame;
    private boolean isNewJoin;
}
