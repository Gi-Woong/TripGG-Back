package com.tripgg.place.dto;

import lombok.Data;

@Data
public class PlaceSearchRequest {
    private String keyword;
    private String category;
    private String x; // longitude
    private String y; // latitude
    private Integer radius;
    private Integer page;
    private Integer size;
    private String sort;
}
