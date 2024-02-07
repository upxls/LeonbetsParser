package com.example.model;

import lombok.Data;

@Data
public class LeagueDto {
    private String id;
    private String name;
    private Boolean top;
    private TinySportDto sport;
}
