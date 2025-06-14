package com.alpaca.futsal_performance_lab_back.controller;

import com.alpaca.futsal_performance_lab_back.dto.response.summary.SummaryResponse;
import com.alpaca.futsal_performance_lab_back.repository.SummaryRepository;
import com.alpaca.futsal_performance_lab_back.service.summary.SummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/summary")
public class SummaryController {

    private final SummaryService summaryService;

    @GetMapping("/{gameId}/{userId}")
    public ResponseEntity<SummaryResponse> getSummary(
            @PathVariable Integer gameId,
            @PathVariable String userId
    ) {
        return summaryService.getSummary(gameId, userId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
}

