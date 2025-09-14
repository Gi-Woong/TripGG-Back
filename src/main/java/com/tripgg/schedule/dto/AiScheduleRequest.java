package com.tripgg.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiScheduleRequest {
    
    private Integer userId;
    private String title;
    private String description;
    private Boolean isAiGenerated;
    
    private String startDate;
    private String endDate;
    
    private String region;
    private List<String> keywords;
    private String companion;
    private String transportation;
    private String language;
}
