package com.tripgg.video.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoRequest {
    
    private String title;
    private String videoUrl;
    private String description;
    private String tags;
}
