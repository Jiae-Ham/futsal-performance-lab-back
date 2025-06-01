package com.alpaca.futsal_performance_lab_back.service.utils;

import com.alpaca.futsal_performance_lab_back.exception.lobby.AccessDeniedToGameException;
import com.alpaca.futsal_performance_lab_back.repository.GameAssignRepository;
import com.alpaca.futsal_performance_lab_back.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidateHost {
    private final GameAssignRepository gameAssignRepository;
    public void requireHostRole(int gameId, String userId) {
        if (!gameAssignRepository.existsByGame_GameIdAndAppUser_UserIdAndHostTrue(gameId, userId)) {
            throw AccessDeniedToGameException.notHost();
        }
    }
}
