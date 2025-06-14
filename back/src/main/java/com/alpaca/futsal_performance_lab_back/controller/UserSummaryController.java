package com.alpaca.futsal_performance_lab_back.controller;

import com.alpaca.futsal_performance_lab_back.dto.response.summary.UserSummaryResponseDTO;
import com.alpaca.futsal_performance_lab_back.service.summary.UserStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/summary")
@RequiredArgsConstructor
public class UserSummaryController {
    private final UserStatsService userStatsService;

    /**
     * 현재 로그인된 사용자의 최근 7세트 평균 능력치 점수 조회
     * @param userDetails 로그인된 사용자 정보
     * @return 6개 능력치 평균 점수
     */
    @GetMapping("/me/stats/recent")
    public ResponseEntity<UserSummaryResponseDTO> getRecentStats(@AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();  // user_id (기본적으로 username으로 매핑됨)
        UserSummaryResponseDTO stats = userStatsService.getRecentSevenSetsStats(userId);
        return ResponseEntity.ok(stats);
    }


}
