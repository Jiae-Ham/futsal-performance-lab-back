package com.alpaca.futsal_performance_lab_back.service.user;

import com.alpaca.futsal_performance_lab_back.dto.auth.AppUserDTO;
import com.alpaca.futsal_performance_lab_back.dto.request.user.AppUserSignUpRequestDTO;
import com.alpaca.futsal_performance_lab_back.security.jwt.JwtToken;
import org.springframework.web.multipart.MultipartFile;

public interface AppUserService {
    JwtToken login(String userId, String userPw);
    void logout(String token);
    void register(AppUserSignUpRequestDTO dto);
    AppUserDTO getMyPage(String userId);
    AppUserDTO updateUserProfile(String userId, AppUserDTO dto, MultipartFile image);

}
