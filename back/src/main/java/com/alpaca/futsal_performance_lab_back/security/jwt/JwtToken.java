package com.alpaca.futsal_performance_lab_back.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class JwtToken {
    private String grantToken;
    private String accessToken;
}
