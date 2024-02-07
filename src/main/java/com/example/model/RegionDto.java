package com.example.model;

import lombok.Data;

import java.util.List;

@Data
public class RegionDto {
    private String id;
    private List<SportInternalLeagueDto> leagues;
}
