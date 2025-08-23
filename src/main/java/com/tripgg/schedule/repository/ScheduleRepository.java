package com.tripgg.schedule.repository;

import com.tripgg.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    
    // 사용자별 일정 목록 조회
    List<Schedule> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    // AI 생성 일정 조회
    List<Schedule> findByUserIdAndIsAiGeneratedTrue(Long userId);
    
    // 제목으로 일정 검색
    List<Schedule> findByUserIdAndTitleContaining(Long userId, String title);
    
    // 사용자별 일정 개수 조회
    @Query("SELECT COUNT(s) FROM Schedule s WHERE s.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
}

