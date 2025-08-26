package com.tripgg.video.entity;

import com.tripgg.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "videos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Video {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "title")
    private String title;
    
    @Column(name = "video_url")
    private String videoUrl;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "tags", columnDefinition = "TEXT")
    private String tags;
    
    @Column(name = "likes")
    @Builder.Default
    private Integer likes = 0;
    
    @Column(name = "views")
    @Builder.Default
    private Integer views = 0;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}

