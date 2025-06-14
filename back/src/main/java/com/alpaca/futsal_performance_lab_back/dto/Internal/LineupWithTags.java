package com.alpaca.futsal_performance_lab_back.dto.Internal;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
public class LineupWithTags {
    private String redTeamFormation;
    private String blueTeamFormation;
    private String timestamp;
    private Map<String, PlayerInfo> redTeam;
    private Map<String, PlayerInfo> blueTeam;

}

