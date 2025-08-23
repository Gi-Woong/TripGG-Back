package com.tripgg.schedule.entity;

import com.tripgg.place.entity.Place;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Place place;
    
    @Column(name = "day")
    private Integer day;
    
    @Column(name = "order_in_day")
    private Integer orderInDay;
    
    @Column(name = "memo", columnDefinition = "TEXT")
    private String memo;
}

