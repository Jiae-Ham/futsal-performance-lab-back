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
    public PlayerStatDto getPlayerStatByUserId(Long userId) {
        String sql = """
            SELECT 
                u.id as user_id,
                u.name as user_name,
                COUNT(DISTINCT g.id) as total_games,
                SUM(ps.goals) as total_goals,
                SUM(ps.assists) as total_assists,
                AVG(ps.distance) as avg_distance,
                AVG(ps.avg_speed) as avg_speed,
                MAX(ps.max_speed) as max_speed,
                AVG(ps.pass_accuracy) as avg_pass_accuracy,
                MAX(g.created_at) as last_game_date
            FROM 
                app_user u
                LEFT JOIN game_assign ga ON u.id = ga.user_id
                LEFT JOIN game g ON ga.game_id = g.id
                LEFT JOIN player_stats ps ON ga.id = ps.game_assign_id
            WHERE 
                u.id = ?
            GROUP BY 
                u.id, u.name
        """;

        return jdbcTemplate.queryForObject(sql, new Object[]{userId}, new PlayerStatRowMapper());
    }

    /**
     * 사용자 게임별 통계 정보 조회
     * 
     * @param userId 사용자 ID
     * @return 사용자 게임별 통계 정보 목록
     */
    public List<GameStatDto> getGameStatsByUserId(Long userId) {
        String sql = """
            SELECT 
                g.id as game_id,
                g.created_at as game_date,
                s.name as stadium_name,
                ps.goals,
                ps.assists,
                ps.distance,
                ps.avg_speed,
                ps.max_speed,
                ps.pass_accuracy,
                ps.shots,
                ps.shots_on_target
            FROM 
                game g
                JOIN game_assign ga ON g.id = ga.game_id
                JOIN stadium s ON g.stadium_id = s.id
                JOIN player_stats ps ON ga.id = ps.game_assign_id
            WHERE 
                ga.user_id = ?
            ORDER BY 
                g.created_at DESC
            LIMIT 10
        """;

        return jdbcTemplate.query(sql, new Object[]{userId}, new GameStatRowMapper());
    }

    /**
     * 선수 통계 정보 RowMapper
     */
    private static class PlayerStatRowMapper implements RowMapper<PlayerStatDto> {
        @Override
        public PlayerStatDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            return PlayerStatDto.builder()
                    .userId(rs.getLong("user_id"))
                    .userName(rs.getString("user_name"))
                    .totalGames(rs.getInt("total_games"))
                    .totalGoals(rs.getInt("total_goals"))
                    .totalAssists(rs.getInt("total_assists"))
                    .avgDistance(rs.getDouble("avg_distance"))
                    .avgSpeed(rs.getDouble("avg_speed"))
                    .maxSpeed(rs.getDouble("max_speed"))
                    .avgPassAccuracy(rs.getDouble("avg_pass_accuracy"))
                    .lastGameDate(rs.getObject("last_game_date", LocalDateTime.class))
                    .build();
        }
    }

    /**
     * 게임 통계 정보 RowMapper
     */
    private static class GameStatRowMapper implements RowMapper<GameStatDto> {
        @Override
        public GameStatDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            return GameStatDto.builder()
                    .gameId(rs.getLong("game_id"))
                    .gameDate(rs.getObject("game_date", LocalDateTime.class))
                    .stadiumName(rs.getString("stadium_name"))
                    .goals(rs.getInt("goals"))
                    .assists(rs.getInt("assists"))
                    .distance(rs.getDouble("distance"))
                    .avgSpeed(rs.getDouble("avg_speed"))
                    .maxSpeed(rs.getDouble("max_speed"))
                    .passAccuracy(rs.getDouble("pass_accuracy"))
                    .shots(rs.getInt("shots"))
                    .shotsOnTarget(rs.getInt("shots_on_target"))
                    .build();
        }
    }
}
