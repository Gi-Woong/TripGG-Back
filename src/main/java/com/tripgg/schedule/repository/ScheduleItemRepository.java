package com.tripgg.schedule.repository;

import com.tripgg.schedule.entity.ScheduleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleItemRepository extends JpaRepository<ScheduleItem, Integer> {
    
    // 일정 ID로 일정 아이템들 조회
    List<ScheduleItem> findByScheduleId(Integer scheduleId);
    
    // 일정 ID와 날짜로 일정 아이템들 조회
    List<ScheduleItem> findByScheduleIdAndDay(Integer scheduleId, Integer day);
    
    // 일정 ID로 일정 아이템 개수 조회
    long countByScheduleId(Integer scheduleId);
}
