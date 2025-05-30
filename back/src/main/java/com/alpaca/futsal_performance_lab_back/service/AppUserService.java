package com.alpaca.futsal_performance_lab_back.service;

import com.alpaca.futsal_performance_lab_back.dto.AppUserSignUpRequestDTO;
import com.alpaca.futsal_performance_lab_back.jwt.JwtToken;
import org.springframework.stereotype.Service;

@Service
public interface AppUserService {
    JwtToken login(String userId, String userPw);
    void logout(String token);
    void register(AppUserSignUpRequestDTO dto);

}
