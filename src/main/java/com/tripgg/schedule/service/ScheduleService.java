package com.tripgg.schedule.service;

import com.tripgg.schedule.entity.Schedule;
import com.tripgg.schedule.repository.ScheduleRepository;
import com.tripgg.user.entity.User;
import com.tripgg.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {
    
    private final ScheduleRepository scheduleRepository;
    private final UserService userService;
    
    // 일정 생성
    @Transactional
    public Schedule createSchedule(Schedule schedule, Long userId) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        schedule.setUser(user);
        return scheduleRepository.save(schedule);
    }
    
    // 일정 조회
    public Optional<Schedule> getScheduleById(Long id) {
        return scheduleRepository.findById(id);
    }
    
    // 사용자별 일정 목록 조회
    public List<Schedule> getSchedulesByUserId(Long userId) {
        return scheduleRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    // AI 생성 일정 조회
    public List<Schedule> getAiGeneratedSchedules(Long userId) {
        return scheduleRepository.findByUserIdAndIsAiGeneratedTrue(userId);
    }
    
    // 일정 검색
    public List<Schedule> searchSchedules(Long userId, String title) {
        return scheduleRepository.findByUserIdAndTitleContaining(userId, title);
    }
    
    // 일정 수정
    @Transactional
    public Schedule updateSchedule(Long id, Schedule scheduleDetails) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("일정을 찾을 수 없습니다."));
        
        if (scheduleDetails.getTitle() != null) {
            schedule.setTitle(scheduleDetails.getTitle());
        }
        if (scheduleDetails.getDescription() != null) {
            schedule.setDescription(scheduleDetails.getDescription());
        }
        if (scheduleDetails.getIsAiGenerated() != null) {
            schedule.setIsAiGenerated(scheduleDetails.getIsAiGenerated());
        }
        
        return scheduleRepository.save(schedule);
    }
    
    // 일정 삭제
    @Transactional
    public void deleteSchedule(Long id) {
        scheduleRepository.deleteById(id);
    }
    
    // 사용자별 일정 개수 조회
    public long getScheduleCountByUserId(Long userId) {
        return scheduleRepository.countByUserId(userId);
    }
}

