package com.alpaca.futsal_performance_lab_back.dto.response.lobby;

import java.util.concurrent.atomic.AtomicBoolean;

public record GameJoinResponse(
        int gameId,
        AtomicBoolean isNewGame,
        boolean isNewJoin
) {}
