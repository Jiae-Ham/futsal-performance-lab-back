package com.alpaca.futsal_performance_lab_back.controller;

import com.alpaca.futsal_performance_lab_back.dto.request.user.AppUserLoginRequestDTO;
import com.alpaca.futsal_performance_lab_back.dto.request.user.AppUserSignUpRequestDTO;
import com.alpaca.futsal_performance_lab_back.jwt.JwtToken;
import com.alpaca.futsal_performance_lab_back.service.user.AppUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AppUserService appUserService;

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<JwtToken> login(@RequestBody AppUserLoginRequestDTO loginRequest) {
        log.info("[AuthController] login() 진입 - userId: {}", loginRequest.getUserId());
        JwtToken jwtToken = appUserService.login(loginRequest.getUserId(), loginRequest.getPassword());
        log.info("[AuthController] 로그인 성공 - accessToken: {}", jwtToken.getAccessToken());
        return ResponseEntity.ok(jwtToken);
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        log.info("[AuthController] logout() 진입");
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            log.info("[AuthController] 로그아웃 처리할 토큰: {}", token);
            appUserService.logout(token);
        } else {
            log.warn("[AuthController] Authorization 헤더가 없거나 Bearer 형식이 아님");
        }
        return ResponseEntity.ok().build();
    }

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody AppUserSignUpRequestDTO signUpRequest) {
        log.info("[AuthController] register() 진입 - userId: {}", signUpRequest.getUserId());
        appUserService.register(signUpRequest);
        log.info("[AuthController] 회원가입 완료 - userId: {}", signUpRequest.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
