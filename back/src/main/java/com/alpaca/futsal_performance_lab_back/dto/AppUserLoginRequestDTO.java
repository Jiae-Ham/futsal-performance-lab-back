package com.alpaca.futsal_performance_lab_back.dto;

import lombok.Data;

@Data
public class AppUserLoginRequestDTO {
    private String userId;
    private String password;
}
