package com.alpaca.futsal_performance_lab_back.exception.lobby;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }

    public static UserNotFoundException of(String userId) {
        return new UserNotFoundException("사용자 [" + userId + "] 를 찾을 수 없습니다.");
    }
}
