package com.example.model;

import lombok.Data;

import java.util.List;

@Data
public class SportDto {
    private String name;
    private String id;
    private List<RegionDto> regions;
}
