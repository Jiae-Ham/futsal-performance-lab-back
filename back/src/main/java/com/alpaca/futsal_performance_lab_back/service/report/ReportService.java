package com.alpaca.futsal_performance_lab_back.service.report;

import com.alpaca.futsal_performance_lab_back.dto.response.report.GameReportResponse;
import com.alpaca.futsal_performance_lab_back.entity.Game;
import com.alpaca.futsal_performance_lab_back.entity.AppUser;
import com.alpaca.futsal_performance_lab_back.entity.GamePerformance;
import com.alpaca.futsal_performance_lab_back.repository.GameRepository;
import com.alpaca.futsal_performance_lab_back.repository.GamePerformanceRepository;
import com.alpaca.futsal_performance_lab_back.repository.AppUserRepository;
import com.alpaca.futsal_performance_lab_back.exception.login.UserNotFoundException;
import com.alpaca.futsal_performance_lab_back.exception.game.GameNotFoundException;

import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class ReportService {

    private final GameRepository gameRepository;
    private final AppUserRepository appUserRepository;
    private final GamePerformanceRepository gamePerformanceRepository;

    public ReportService(GameRepository gameRepository,
                         AppUserRepository appUserRepository,
                         GamePerformanceRepository gamePerformanceRepository) {
        this.gameRepository = gameRepository;
        this.appUserRepository = appUserRepository;
        this.gamePerformanceRepository = gamePerformanceRepository;
    }

    public GameReportResponse getGameReport(Integer gameId, String userId) {
        // 게임 존재 확인
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException("Game not found with id: " + gameId));

        // 사용자 존재 확인 (findByUserId 사용)
        AppUser user = appUserRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        // 성능 데이터 조회 (타입 맞춰서)
        GamePerformance performance = gamePerformanceRepository.findByGameIdAndUserId(gameId, userId)
                .orElse(createDummyPerformance(gameId, userId));

        return GameReportResponse.builder()
                .gameId(gameId)         // Integer
                .userId(userId)         // String
                .playerName(user.getName())
                .gameDate(game.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .totalPlayTime(performance.getTotalPlayTime())
                .avgSpeed(performance.getAvgSpeed())
                .totalDistance(performance.getTotalDistance())
                .sprintCount(performance.getSprintCount())
                .passAccuracy(performance.getPassAccuracy())
                .shotAccuracy(performance.getShotAccuracy())
                .gameResult(determineGameResult(game, userId))
                // .finalScore(game.getFinalScore())
                .build();
    }

    private GamePerformance createDummyPerformance(Integer gameId, String userId) {
        return GamePerformance.builder()
                .gameId(gameId)         // Integer
                .userId(userId)         // String
                .totalPlayTime(45)
                .avgSpeed(12.5)
                .totalDistance(3200)
                .sprintCount(15)
                .passAccuracy(78.5)
                .shotAccuracy(66.7)
                .build();
    }

    private String determineGameResult(Game game, String userId) {
        return "WIN"; // 테스트용 고정값
    }
}