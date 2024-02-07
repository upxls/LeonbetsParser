package com.example.model;

import lombok.Data;

import java.util.List;

@Data
public class MarketDto {
    String name;
    List<RunnersDto> runners;
}
