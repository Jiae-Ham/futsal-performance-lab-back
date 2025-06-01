package com.alpaca.futsal_performance_lab_back.service.user;

import com.alpaca.futsal_performance_lab_back.dto.request.user.AppUserSignUpRequestDTO;
import com.alpaca.futsal_performance_lab_back.security.jwt.JwtToken;
import org.springframework.stereotype.Service;

@Service
public interface AppUserService {
    JwtToken login(String userId, String userPw);
    void logout(String token);
    void register(AppUserSignUpRequestDTO dto);

}
