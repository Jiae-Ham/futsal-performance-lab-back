package com.alpaca.futsal_performance_lab_back.service;

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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LobbyService {

    private final GameRepository gameRepository;
    private final GameAssignRepository gameAssignRepository;
    private final AppUserRepository appUserRepository;
    private final StadiumRepository stadiumRepository;

    // 참여 가능한 게임이 없으면 새로 생성하고, 있으면 해당 게임에 참여시킴
    public int createGameForUser(String userId, int stadiumId) {
        Game activeGame = gameRepository.findFirstByActiveAndStadium_StadiumId(1, stadiumId)
                .orElseGet(() -> {
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

        if (gameAssignRepository.existsByGame_GameIdAndAppUser_UserId(activeGame.getGameId(), userId)) {
            AppUser user = appUserRepository.findById(userId)
                    .orElseThrow(() -> UserNotFoundException.of(userId));

            GameAssign assign = GameAssign.builder()
                    .game(activeGame)
                    .appUser(user)
                    .host(false)
                    .build();

            gameAssignRepository.save(assign);
        }

        return activeGame.getGameId();
    }

    // 유저의 참여 여부를 확인하고 게임의 대기 상태 정보를 제공
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

    // 방장이 준비 완료를 눌렀을 때 게임 상태를 활성화(2)로 변경
    public void markReady(int gameId, String userId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(GameNotFoundException::new);

        if (!gameAssignRepository.existsByGame_GameIdAndAppUser_UserIdAndHostTrue(gameId, userId)) {
            throw AccessDeniedToGameException.notHost();
        }

        game.setActive(2);
        gameRepository.save(game);
    }
}

