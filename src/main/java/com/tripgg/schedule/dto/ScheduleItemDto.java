package com.tripgg.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleItemDto {
    private Integer id;
    private Integer scheduleId;
    private Integer placeId;
    private Integer day;
    private Integer orderInDay;
    private String memo;
    
    private String startDate;
    private String startTime;
    private String endTime;
    
    private PlaceDto place;
}
