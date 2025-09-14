package com.tripgg.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDto {
    private Integer id;
    private Integer userId;
    private String title;
    private String description;
    private Boolean isAiGenerated;
    
    private String startDate;
    private String endDate;
    private String createdAt;
}
