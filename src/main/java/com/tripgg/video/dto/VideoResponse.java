package com.tripgg.video.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoResponse {
    
    private Integer id;
    private Integer userId;
    private String userNickname;
    private String title;
    private String videoUrl;
    private String description;
    private String tags;
    private Integer likes;
    private Integer views;
    private LocalDateTime createdAt;
}
