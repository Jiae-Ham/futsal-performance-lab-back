package com.alpaca.futsal_performance_lab_back.exception.game;


public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(String message) {
        super(message);
    }
}
