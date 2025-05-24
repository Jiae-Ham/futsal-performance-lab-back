package com.alpaca.futsal_performance_lab_back.exception.lobby;

public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException() {
        super("게임을 찾을 수 없습니다.");
    }

    public GameNotFoundException(String message) {
        super(message);
    }
}
