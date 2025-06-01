package com.alpaca.futsal_performance_lab_back.exception.game;

import com.fasterxml.jackson.core.JsonProcessingException;

public class GameLineupSerializationException extends RuntimeException {
    public GameLineupSerializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public static GameLineupSerializationException from(JsonProcessingException e) {
        return new GameLineupSerializationException("라인업 직렬화 중 오류 발생", e);
    }
}
