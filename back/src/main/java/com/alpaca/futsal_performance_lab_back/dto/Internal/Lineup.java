package com.alpaca.futsal_performance_lab_back.dto.Internal;

import lombok.Getter;

import java.util.Map;

@Getter
public class Lineup {
    private String redTeamFormation;
    private String blueTeamFormation;
    private String timestamp;
    private Map<String, String> redTeam;
    private Map<String, String> blueTeam;
}
