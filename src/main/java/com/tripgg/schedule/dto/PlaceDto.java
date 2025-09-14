package com.tripgg.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceDto {
    private Integer id;
    private String name;
    private String category;
    private String description;
    private String address;
    private Double latitude;
    private Double longitude;
}
