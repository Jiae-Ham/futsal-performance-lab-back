package com.alpaca.futsal_performance_lab_back.jwt;

import com.alpaca.futsal_performance_lab_back.entity.AppUser;
import com.alpaca.futsal_performance_lab_back.service.TokenBlacklistService;
import com.alpaca.futsal_performance_lab_back.service.TokenBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklistService tokenBlacklistService;
    private static final AntPathMatcher matcher = new AntPathMatcher();

    // ✅ 공개 URL 목록 - 토큰 없이도 접근 허용
    private static final List<String> PUBLIC_PATTERNS = List.of(
            "/api/auth/**"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();
        log.info("[JwtAuthFilter] 요청 URI: {}", uri);

        String token = resolveToken(request);

        boolean isPublic = PUBLIC_PATTERNS.stream()
                .anyMatch(pattern -> matcher.match(pattern, uri));

        if (isPublic) {
            log.info("[JwtAuthFilter] 공개 URL이므로 필터 통과: {}", uri);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (token != null && jwtTokenProvider.validateToken(token)) {
                log.info("[JwtAuthFilter] 유효한 토큰 확인: {}", token);

                if (tokenBlacklistService.isBlacklisted(token)) {
                    log.warn("[JwtAuthFilter] 블랙리스트에 등록된 토큰입니다.");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json;charset=utf-8");
                    response.getWriter().write("{\"error\": \"로그아웃된 토큰입니다.\"}");
                    return;
                }

                Authentication auth = jwtTokenProvider.getAuthentication(token);
                AppUser user = (AppUser) auth.getPrincipal();
                log.info("인증된 사용자: {}", user.getUserId()); // ✅ 특정 필드만 출력
                if (auth != null) {
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    log.info("[JwtAuthFilter] 인증 객체 설정 완료: {}", auth.getName());
                } else {
                    log.warn("[JwtAuthFilter] 인증 객체 생성 실패 - null 반환");
                }

                filterChain.doFilter(request, response);
            } else {
                log.warn("[JwtAuthFilter] Jwt 유효하지 않은 토큰 또는 토큰 없음");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=utf-8");
                response.getWriter().write("{\"error\" : \"Jwt 토큰이 없거나 유효하지 않습니다.\"}");
            }
        } catch (StackOverflowError e) {
            log.error("[JwtAuthFilter] StackOverflowError 발생!", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\": \"StackOverflowError: 재귀 오류가 발생했습니다.\"}");
        } catch (Throwable t) { // ❗ Throwable로 바꿔야 Error 계열도 잡힘
            log.error("[JwtAuthFilter] 알 수 없는 오류 발생: {}", t.getMessage(), t);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\": \"서버 내부 오류가 발생했습니다.\"}");
        }
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.debug("[JwtAuthFilter] Authorization 헤더: {}", bearerToken);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
