package com.alpaca.futsal_performance_lab_back.service.utils;

import org.springframework.stereotype.Component;

@Component
public class ScoreNormalizer {

    /** min~max → 0~10 선형 변환 (클램핑) */
    public double toTenScale(double raw, double min, double max) {
        if (max <= min) return 0;
        double v = (raw - min) / (max - min) * 10;
        return Math.max(0, Math.min(10, v));
    }

    /** 소수 2째자리 반올림 도움 메서드 */
    public double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
