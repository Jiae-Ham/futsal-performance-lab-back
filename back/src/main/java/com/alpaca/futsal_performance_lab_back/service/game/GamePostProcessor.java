package com.alpaca.futsal_performance_lab_back.service.game;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GamePostProcessor {

    private final JdbcTemplate clickHouseJdbcTemplate;

    @Async
    public void processAfterGameEnd(int gameId) {
        log.info("[Async] 게임 {} 종료 후 ClickHouse 처리 시작", gameId);

        try {
            // 예시: 경기 단위 통계 INSERT 또는 UPDATE
            String sql = """
            """;

            clickHouseJdbcTemplate.update(sql, gameId, gameId);

            log.info("[Async] 게임 {} 처리 완료", gameId);

        } catch (Exception e) {
            log.error("[Async] 게임 {} 처리 중 오류 발생: {}", gameId, e.getMessage(), e);
        }
    }
}
