package com.tripgg.chat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_rooms")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ChatRoom {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "room_name", nullable = false)
    private String roomName;  // 예: "서울 채팅방", "부산 채팅방"
    
    @Column(name = "latitude", nullable = false)
    private Double latitude;  // 채팅방 중심 위도
    
    @Column(name = "longitude", nullable = false)
    private Double longitude; // 채팅방 중심 경도
    
    @Column(name = "location_name", nullable = false)
    private String locationName; // 예: "서울", "부산"
    
    @Column(name = "radius_km", nullable = false)
    private Double radiusKm;  // 채팅방 반경 (km)
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive; // 채팅방 활성화 상태
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
