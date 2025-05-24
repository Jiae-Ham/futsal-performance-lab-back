package com.alpaca.futsal_performance_lab_back.exception.lobby;

public class AccessDeniedToGameException extends RuntimeException {
    public AccessDeniedToGameException(String message) {
        super(message);
    }

    public static AccessDeniedToGameException notParticipant() {
        return new AccessDeniedToGameException("해당 게임에 참여하지 않은 사용자입니다.");
    }

    public static AccessDeniedToGameException notHost() {
        return new AccessDeniedToGameException("해당 게임의 방장이 아닙니다.");
    }
}
