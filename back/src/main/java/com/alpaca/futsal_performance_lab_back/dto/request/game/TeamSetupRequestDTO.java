package com.alpaca.futsal_performance_lab_back.dto.request.game;

import lombok.Data;

import java.util.Map;

@Data
public class TeamSetupRequestDTO {
    private String redTeamFormation;
    private String blueTeamFormation;
    private String timestamp;
    private Map<String, String> redTeam;
    private Map<String, String> blueTeam;
}

