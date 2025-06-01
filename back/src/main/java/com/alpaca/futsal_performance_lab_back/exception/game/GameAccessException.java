package com.alpaca.futsal_performance_lab_back.exception.game;

public class GameAccessException extends RuntimeException {
    public GameAccessException(String message) {
        super(message);
    }

    // 게임 ID로 조회 실패
    public static GameAccessException gameNotFound() {
        return new GameAccessException("해당 게임을 찾을 수 없습니다.");
    }

    // 세트 접근 권한 없음
    public static GameAccessException noAccessToSet() {
        return new GameAccessException("이 게임 세트에 접근할 수 없습니다.");
    }

    // 게임이 이미 종료되었을 때
    public static GameAccessException gameAlreadyFinished() {
        return new GameAccessException("해당 게임은 이미 종료되었습니다.");
    }

    // 요청자가 호스트가 아닌 경우
    public static GameAccessException notHost() {
        return new GameAccessException("요청자는 이 게임의 방장이 아닙니다.");
    }
}
