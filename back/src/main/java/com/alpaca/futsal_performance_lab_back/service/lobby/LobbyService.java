package com.alpaca.futsal_performance_lab_back.service.lobby;

import com.alpaca.futsal_performance_lab_back.dto.response.lobby.GameJoinResponse;
import com.alpaca.futsal_performance_lab_back.dto.response.lobby.LobbyStatusResponse;
import com.alpaca.futsal_performance_lab_back.entity.AppUser;
import com.alpaca.futsal_performance_lab_back.entity.Game;
import com.alpaca.futsal_performance_lab_back.entity.GameAssign;
import com.alpaca.futsal_performance_lab_back.entity.Stadium;
import com.alpaca.futsal_performance_lab_back.exception.lobby.AccessDeniedToGameException;
import com.alpaca.futsal_performance_lab_back.exception.lobby.GameNotFoundException;
import com.alpaca.futsal_performance_lab_back.exception.lobby.UserNotFoundException;
import com.alpaca.futsal_performance_lab_back.repository.AppUserRepository;
import com.alpaca.futsal_performance_lab_back.repository.GameAssignRepository;
import com.alpaca.futsal_performance_lab_back.repository.GameRepository;
import com.alpaca.futsal_performance_lab_back.repository.StadiumRepository;
import com.alpaca.futsal_performance_lab_back.service.utils.ValidateHost;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class LobbyService {

    private final GameRepository gameRepository;
    private final GameAssignRepository gameAssignRepository;
    private final AppUserRepository appUserRepository;
    private final StadiumRepository stadiumRepository;
    private final ValidateHost validateHost; // ✅ 추가된 의존성

    public GameJoinResponse createGameForUser(String userId, int stadiumId) {
        AtomicBoolean isNewGame = new AtomicBoolean(false);
        boolean isNewJoin = false;

        Game activeGame = gameRepository.findFirstByActiveAndStadium_StadiumId(1, stadiumId)
                .orElseGet(() -> {
                    isNewGame.set(true);
                    Stadium stadium = stadiumRepository.findById(stadiumId)
                            .orElseThrow(GameNotFoundException::new);

                    Game newGame = Game.builder()
                            .createdAt(LocalDateTime.now())
                            .active(1)
                            .stadium(stadium)
                            .build();

                    Game savedGame = gameRepository.save(newGame);

                    AppUser host = appUserRepository.findById(userId)
                            .orElseThrow(() -> UserNotFoundException.of(userId));

                    GameAssign hostAssign = GameAssign.builder()
                            .game(savedGame)
                            .appUser(host)
                            .host(true)
                            .build();

                    gameAssignRepository.save(hostAssign);
                    return savedGame;
                });

        if (!gameAssignRepository.existsByGame_GameIdAndAppUser_UserId(activeGame.getGameId(), userId)) {
            isNewJoin = true;

            AppUser user = appUserRepository.findById(userId)
                    .orElseThrow(() -> UserNotFoundException.of(userId));

            GameAssign assign = GameAssign.builder()
                    .game(activeGame)
                    .appUser(user)
                    .host(false)
                    .build();

            gameAssignRepository.save(assign);
        }

        return new GameJoinResponse(activeGame.getGameId(), isNewGame, isNewJoin);
    }

    public LobbyStatusResponse getLobbyStatus(int gameId, String userId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(GameNotFoundException::new);

        if (gameAssignRepository.existsByGame_GameIdAndAppUser_UserId(gameId, userId)) {
            throw AccessDeniedToGameException.notParticipant();
        }

        int playerCount = gameAssignRepository.countByGame_GameId(gameId);
        int isActive = game.getActive();

        return new LobbyStatusResponse(playerCount, isActive);
    }

    public void markReady(int gameId, String userId) {
        // ✅ 방장 확인
        validateHost.requireHostRole(gameId, userId);

        Game game = gameRepository.findById(gameId)
                .orElseThrow(GameNotFoundException::new);

        game.setActive(2);
        gameRepository.save(game);
    }
}



