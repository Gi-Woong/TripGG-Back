package com.tripgg.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "kakao_id", unique = true, nullable = false)
    private String kakaoId;
    
    @Column(name = "nickname")
    private String nickname;
    
    @Column(name = "profile_image_url")
    private String profileImageUrl;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // 연관관계 - 순환 참조 방지를 위해 제거
    // @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    // private List<Schedule> schedules;
    
    // @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    // private List<Chat> chats;
    
    // @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    // private List<Video> videos;
}
