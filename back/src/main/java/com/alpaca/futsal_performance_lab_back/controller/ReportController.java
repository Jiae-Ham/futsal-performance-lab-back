package com.alpaca.futsal_performance_lab_back.controller;

import com.alpaca.futsal_performance_lab_back.dto.response.report.GameReportResponse;
import com.alpaca.futsal_performance_lab_back.service.report.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/report")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/{gameId}/{userId}")
    public ResponseEntity<GameReportResponse> getGameReport(
            @PathVariable Integer gameId,
            @PathVariable String  userId) {

        GameReportResponse report = reportService.getGameReport(gameId, userId);
        return ResponseEntity.ok(report);
    }
}