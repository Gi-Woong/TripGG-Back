package com.tripgg.schedule.entity;

import com.tripgg.place.entity.SchedulePlaces;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "schedule_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private SchedulePlaces schedulePlaces;
    
    @Column(name = "day")
    private Integer day;
    
    @Column(name = "order_in_day")
    private Integer orderInDay;
    
    @Column(name = "memo", columnDefinition = "TEXT")
    private String memo;

    // 신규추가
    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

