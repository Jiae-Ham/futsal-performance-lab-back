package com.alpaca.futsal_performance_lab_back.controller;

import com.alpaca.futsal_performance_lab_back.dto.response.lobby.PerformanceSummaryResponseDto;
import com.alpaca.futsal_performance_lab_back.service.PerformanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 사용자 성능 통계 요약 API 컨트롤러
 * 사용자의 성능 통계 데이터를 요약하여 제공하는 API를 처리합니다.
 */
@RestController
@RequestMapping("/api/performance")
public class PerformanceController {

    private final PerformanceService performanceService;

    @Autowired
    public PerformanceController(PerformanceService performanceService) {
        this.performanceService = performanceService;
    }

    /**
     * 사용자 성능 통계 요약 조회 API
     * 
     * @param userId 사용자 ID
     * @return 사용자 성능 통계 요약 정보
     */
    @GetMapping("/{userId}")
    public ResponseEntity<PerformanceSummaryResponseDto> getPerformanceSummary(@PathVariable Long userId) {
        PerformanceSummaryResponseDto performanceSummary = performanceService.getPerformanceSummary(userId);
        return ResponseEntity.ok(performanceSummary);
    }
}
