package com.alpaca.futsal_performance_lab_back.controller;

import com.alpaca.futsal_performance_lab_back.dto.response.summary.SummaryNormalizedResponse;
import com.alpaca.futsal_performance_lab_back.dto.response.summary.SummaryResponse;
import com.alpaca.futsal_performance_lab_back.service.summary.SummaryNormalizedService;
import com.alpaca.futsal_performance_lab_back.service.summary.SummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/summary")
@RequiredArgsConstructor
public class SummaryController {

    private final SummaryService            summaryService;      // 원본
    private final SummaryNormalizedService  normalizedService;   // 0-10 정규화

    @GetMapping("/raw/{gameId}/{userId}")
    public ResponseEntity<List<SummaryResponse>> getSummariesRaw(
            @PathVariable Integer gameId,
            @PathVariable String  userId) {

        List<SummaryResponse> list = summaryService.getSummaries(gameId, userId);
        return list.isEmpty()
                ? ResponseEntity.noContent().build()   // 204
                : ResponseEntity.ok(list);             // 200 + JSON 배열
    }

    @GetMapping("/normalized/{gameId}/{userId}")
    public ResponseEntity<List<SummaryNormalizedResponse>> getSummariesNormalized(
            @PathVariable Integer gameId,
            @PathVariable String  userId) {

        List<SummaryNormalizedResponse> list = normalizedService.getSummaries(gameId, userId);
        return list.isEmpty()
                ? ResponseEntity.noContent().build()   // 204
                : ResponseEntity.ok(list);             // 200
    }
}

