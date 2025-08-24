package com.tripgg.schedule.controller;

import com.tripgg.common.dto.ApiResponse;
import com.tripgg.schedule.entity.Schedule;
import com.tripgg.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class ScheduleController {
    
    private final ScheduleService scheduleService;
    
    // 전체 일정 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<Schedule>>> getAllSchedules() {
        List<Schedule> schedules = scheduleService.getAllSchedules();
        return ResponseEntity.ok(ApiResponse.success("전체 일정을 성공적으로 조회했습니다.", schedules));
    }
    
    // 일정 생성
    @PostMapping
    public ResponseEntity<ApiResponse<Schedule>> createSchedule(
            @RequestBody Schedule schedule,
            @RequestParam Long userId) {
        Schedule createdSchedule = scheduleService.createSchedule(schedule, userId);
        return ResponseEntity.ok(ApiResponse.success("일정이 성공적으로 생성되었습니다.", createdSchedule));
    }
    
    // 일정 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Schedule>> getScheduleById(@PathVariable Long id) {
        Schedule schedule = scheduleService.getScheduleById(id)
                .orElseThrow(() -> new RuntimeException("일정을 찾을 수 없습니다."));
        return ResponseEntity.ok(ApiResponse.success("일정을 성공적으로 조회했습니다.", schedule));
    }
    
    // 사용자별 일정 목록 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Schedule>>> getSchedulesByUserId(@PathVariable Long userId) {
        List<Schedule> schedules = scheduleService.getSchedulesByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("사용자 일정 목록을 성공적으로 조회했습니다.", schedules));
    }
    
    // AI 생성 일정 조회
    @GetMapping("/user/{userId}/ai-generated")
    public ResponseEntity<ApiResponse<List<Schedule>>> getAiGeneratedSchedules(@PathVariable Long userId) {
        List<Schedule> schedules = scheduleService.getAiGeneratedSchedules(userId);
        return ResponseEntity.ok(ApiResponse.success("AI 생성 일정을 성공적으로 조회했습니다.", schedules));
    }
    
    // 일정 검색
    @GetMapping("/user/{userId}/search")
    public ResponseEntity<ApiResponse<List<Schedule>>> searchSchedules(
            @PathVariable Long userId,
            @RequestParam String title) {
        List<Schedule> schedules = scheduleService.searchSchedules(userId, title);
        return ResponseEntity.ok(ApiResponse.success("일정 검색이 완료되었습니다.", schedules));
    }
    
    // 일정 수정
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Schedule>> updateSchedule(
            @PathVariable Long id,
            @RequestBody Schedule scheduleDetails) {
        Schedule updatedSchedule = scheduleService.updateSchedule(id, scheduleDetails);
        return ResponseEntity.ok(ApiResponse.success("일정이 성공적으로 수정되었습니다.", updatedSchedule));
    }
    
    // 일정 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.ok(ApiResponse.success("일정이 성공적으로 삭제되었습니다.", "삭제 완료"));
    }
    
    // 사용자별 일정 개수 조회
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<ApiResponse<Long>> getScheduleCount(@PathVariable Long userId) {
        long count = scheduleService.getScheduleCountByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("일정 개수를 성공적으로 조회했습니다.", count));
    }
}

