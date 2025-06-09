package com.alpaca.futsal_performance_lab_back.repository;

import com.alpaca.futsal_performance_lab_back.dto.response.lobby.GameStatDto;
import com.alpaca.futsal_performance_lab_back.dto.response.lobby.PlayerStatDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 성능 통계 데이터 접근 레포지토리
 * 사용자의 성능 통계 데이터를 데이터베이스에서 조회하는 기능을 담당합니다.
 */
@Repository
public class PerformanceRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PerformanceRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 사용자 기본 통계 정보 조회
     * 
     * @param userId 사용자 ID
     * @return 사용자 기본 통계 정보
     */
    public PlayerStatDto getPlayerStatByUserId(String userId) {
        String sql = """
        WITH user_games AS (
            SELECT DISTINCT g.game_id, g.created_at
            FROM game g
            JOIN game_assign ga ON g.game_id = ga.game_id
            WHERE ga.user_id = ? AND g.active = 5
        ),
        user_performance AS (
            SELECT 
                gp.total_distance,
                gp.total_play_time,
                gp.avg_speed,
                gp.sprint_count,
                gp.pass_accuracy,
                gp.shot_accuracy
            FROM game_performance gp
            WHERE gp.user_id = ?
        )
        SELECT 
            u.user_id as user_id,
            u.name as user_name,
            (SELECT COUNT(*) FROM user_games) as total_games,
            COALESCE(SUM(up.total_distance), 0) as total_distance,
            COALESCE(SUM(up.total_play_time), 0) as total_play_time,
            COALESCE(MAX(up.avg_speed), 0) as max_speed,
            COALESCE(AVG(up.avg_speed), 0) as avg_speed,
            COALESCE(SUM(up.sprint_count), 0) as sprint_count,
            COALESCE(AVG(up.pass_accuracy), 0) as avg_pass_accuracy,
            COALESCE(AVG(up.shot_accuracy), 0) as avg_shot_accuracy,
            COALESCE(AVG(up.total_distance), 0) as avg_distance,
            (SELECT MAX(created_at) FROM user_games) as last_game_date
        FROM 
            app_user u
            LEFT JOIN user_performance up ON 1=1
        WHERE 
            u.user_id = ?
        GROUP BY 
            u.user_id, u.name
    """;

        return jdbcTemplate.queryForObject(sql, new Object[]{userId, userId, userId}, new PlayerStatRowMapper());
    }

    /**
     * 사용자 게임별 통계 정보 조회
     * 
     * @param userId 사용자 ID
     * @return 사용자 게임별 통계 정보 목록
     */
    public List<GameStatDto> getGameStatsByUserId(String userId) {
        String sql = """
        SELECT DISTINCT
            g.game_id as game_id,
            g.created_at as game_date,
            COALESCE(gp.total_distance, 0) as total_distance,
            COALESCE(gp.avg_speed, 0) as avg_speed
        FROM 
            game g
            JOIN game_assign ga ON g.game_id = ga.game_id
            LEFT JOIN game_performance gp ON g.game_id = gp.game_id AND gp.user_id = ?
        WHERE 
            ga.user_id = ?
            AND g.active = 5
        ORDER BY 
            g.created_at DESC
        LIMIT 10
    """;

        return jdbcTemplate.query(sql, new Object[]{userId, userId}, new GameStatRowMapper());
    }

    /**
     * 선수 통계 정보 RowMapper
     */
    private static class PlayerStatRowMapper implements RowMapper<PlayerStatDto> {
        @Override
        public PlayerStatDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            // userId를 String으로 직접 사용
            String userId = rs.getString("user_id");

            return PlayerStatDto.builder()
                    .userId(userId)  // String 그대로 사용
                    .userName(rs.getString("user_name"))
                    .totalGames(rs.getInt("total_games"))
                    .totalGoals(0) // 임시값
                    .totalAssists(0) // 임시값
                    .avgDistance(rs.getDouble("avg_distance") / 1000.0) // m를 km로 변환
                    .avgSpeed(rs.getDouble("avg_speed"))
                    .maxSpeed(rs.getDouble("max_speed"))
                    .avgPassAccuracy(rs.getDouble("avg_pass_accuracy"))
                    .lastGameDate(rs.getObject("last_game_date", LocalDateTime.class))
                    .totalDistance(rs.getLong("total_distance"))
                    .totalPlayTime(rs.getInt("total_play_time"))
                    .sprintCount(rs.getInt("sprint_count"))
                    .caloriesBurned(rs.getDouble("total_distance") * 0.05)
                    .sprintDistance(rs.getInt("sprint_count"))
                    .directionChanges(rs.getInt("sprint_count") * 2)
                    .build();
        }
    }

    /**
     * 게임 통계 정보 RowMapper
     */
    public class GameStatRowMapper implements RowMapper<GameStatDto> {
        @Override
        public GameStatDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            return GameStatDto.builder()
                    .gameId(rs.getLong("game_id"))
                    .gameDate(rs.getObject("game_date", LocalDateTime.class))
                    .stadiumName("테스트 경기장") // 임시값 (stadium 테이블 조인 필요시)
                    .goals(0) // 임시값 (골 데이터가 없으므로)
                    .assists(0) // 임시값 (어시스트 데이터가 없으므로)
                    .distance((double) rs.getInt("total_distance") / 1000.0) // m를 km로 변환
                    .avgSpeed(rs.getDouble("avg_speed"))
                    .maxSpeed(rs.getDouble("avg_speed")) // 임시로 avg_speed 사용
                    .passAccuracy(0.0) // 임시값
                    .shots(0) // 임시값
                    .shotsOnTarget(0) // 임시값
                    .build();
        }
    }
}
