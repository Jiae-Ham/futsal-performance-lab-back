package com.alpaca.futsal_performance_lab_back.service.user;

import com.alpaca.futsal_performance_lab_back.dto.request.user.AppUserSignUpRequestDTO;
import com.alpaca.futsal_performance_lab_back.entity.AppUser;
import com.alpaca.futsal_performance_lab_back.jwt.JwtToken;
import com.alpaca.futsal_performance_lab_back.jwt.JwtTokenProvider;
import com.alpaca.futsal_performance_lab_back.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository appUserRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final TokenBlacklistService tokenBlacklistService;

    public JwtToken login(String userId, String userPw) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userId, userPw)
            );
            return jwtTokenProvider.generateToken(authentication);
        } catch (AuthenticationException ex) {
            // 인증에 실패하면 401 Bad Credentials로 내려갑니다
            throw new BadCredentialsException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }
    }

    @Override
    public void logout(String token) {
        long remainingTime = jwtTokenProvider.getTokenRemainingTime(token);
        tokenBlacklistService.blacklistToken(token, remainingTime);
    }

    @Override
    public void register(AppUserSignUpRequestDTO dto) {
        if (appUserRepository.findByUserId(dto.getUserId()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자 ID입니다.");
        }

        AppUser user = AppUserSignUpRequestDTO.fromDTO(dto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        appUserRepository.save(user);
    }
}
