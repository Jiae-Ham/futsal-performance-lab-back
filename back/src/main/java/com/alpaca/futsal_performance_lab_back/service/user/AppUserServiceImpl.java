package com.alpaca.futsal_performance_lab_back.service.user;

import com.alpaca.futsal_performance_lab_back.dto.auth.AppUserDTO;
import com.alpaca.futsal_performance_lab_back.dto.request.user.AppUserSignUpRequestDTO;
import com.alpaca.futsal_performance_lab_back.entity.AppUser;
import com.alpaca.futsal_performance_lab_back.repository.AppUserRepository;
import com.alpaca.futsal_performance_lab_back.security.jwt.JwtToken;
import com.alpaca.futsal_performance_lab_back.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository appUserRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final TokenBlacklistService tokenBlacklistService;
    private final FileUploadService fileUploadService;

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

    @Override
    public AppUserDTO getMyPage(String userId) {
        AppUser user = appUserRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return AppUserDTO.fromEntity(user);
    }

    @Override
    public AppUserDTO updateUserProfile(String userId, AppUserDTO dto, MultipartFile image) {
        AppUser user = appUserRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.setName(dto.getName());
        user.setNickname(dto.getNickname());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setMainPosition(dto.getMainPosition());
        user.setDominantFoot(dto.getDominantFoot());
        user.setWeight(dto.getWeight());
        user.setHeight(dto.getHeight());
        user.setBirthDate(dto.getBirthDate());


        if (image != null && !image.isEmpty()) {
            String fileUrl = fileUploadService.upload(image);
            user.setProfileImageUrl(fileUrl);
        }

        user.setUpdatedAt(LocalDateTime.now());
        appUserRepository.save(user);
        return AppUserDTO.fromEntity(user);
    }

}

