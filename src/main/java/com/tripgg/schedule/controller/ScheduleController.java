package com.tripgg.schedule.controller;

import com.tripgg.auth.util.SecurityUtil;
import com.tripgg.common.dto.ApiResponse;
import com.tripgg.schedule.dto.AiScheduleRequest;
import com.tripgg.schedule.dto.AiScheduleResponse;
import com.tripgg.schedule.entity.Schedule;
import com.tripgg.schedule.service.ai.GptApiService;
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
    private final GptApiService gptApiService;
    
    // AI 일정 생성
    @PostMapping("/ai-generate")
    public ResponseEntity<ApiResponse<AiScheduleResponse>> generateAiSchedule(@RequestBody AiScheduleRequest request) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        String currentUserNickname = SecurityUtil.getCurrentUserNickname();
        log.info("AI 일정 생성 요청 - 사용자 ID: {}, 닉네임: {}", currentUserId, currentUserNickname);
        
        if (currentUserId == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("인증이 필요합니다."));
        }
        
        try {
            AiScheduleResponse response = gptApiService.generateSchedule(request);
            // GPT 응답을 DB에 저장
            AiScheduleResponse savedResponse = scheduleService.saveAiSchedule(response, currentUserId);
            return ResponseEntity.ok(ApiResponse.success("AI 일정이 성공적으로 생성되었습니다.", savedResponse));
        } catch (Exception e) {
            log.error("AI 일정 생성 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("AI 일정 생성 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
    
    // 전체 일정 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<Schedule>>> getAllSchedules() {
        List<Schedule> schedules = scheduleService.getAllSchedules();
        return ResponseEntity.ok(ApiResponse.success("전체 일정을 성공적으로 조회했습니다.", schedules));
    }
    
    // 일정 생성
    @PostMapping
    public ResponseEntity<ApiResponse<Schedule>> createSchedule(@RequestBody Schedule schedule) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        String currentUserNickname = SecurityUtil.getCurrentUserNickname();
        log.info("일정 생성 요청 - 사용자 ID: {}, 닉네임: {}", currentUserId, currentUserNickname);
        
        if (currentUserId == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("인증이 필요합니다."));
        }
        
        Schedule createdSchedule = scheduleService.createSchedule(schedule, currentUserId);
        return ResponseEntity.ok(ApiResponse.success("일정이 성공적으로 생성되었습니다.", createdSchedule));
    }
    
    // 일정 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Schedule>> getScheduleById(@PathVariable Long id) {
        Schedule schedule = scheduleService.getScheduleById(id)
                .orElseThrow(() -> new RuntimeException("일정을 찾을 수 없습니다."));
        return ResponseEntity.ok(ApiResponse.success("일정을 성공적으로 조회했습니다.", schedule));
    }
    
    // 사용자별 일정 목록 조회 (현재 로그인한 사용자)
    @GetMapping("/my-schedules")
    public ResponseEntity<ApiResponse<List<Schedule>>> getMySchedules() {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        String currentUserNickname = SecurityUtil.getCurrentUserNickname();
        log.info("내 일정 조회 요청 - 사용자 ID: {}, 닉네임: {}", currentUserId, currentUserNickname);
        
        if (currentUserId == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("인증이 필요합니다."));
        }
        
        List<Schedule> schedules = scheduleService.getSchedulesByUserId(currentUserId);
        return ResponseEntity.ok(ApiResponse.success("사용자 일정 목록을 성공적으로 조회했습니다.", schedules));
    }
    
    // AI 생성 일정 조회 (현재 로그인한 사용자)
    @GetMapping("/my-ai-schedules")
    public ResponseEntity<ApiResponse<List<Schedule>>> getMyAiGeneratedSchedules() {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        String currentUserNickname = SecurityUtil.getCurrentUserNickname();
        log.info("내 AI 일정 조회 요청 - 사용자 ID: {}, 닉네임: {}", currentUserId, currentUserNickname);
        
        if (currentUserId == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("인증이 필요합니다."));
        }
        
        List<Schedule> schedules = scheduleService.getAiGeneratedSchedules(currentUserId);
        return ResponseEntity.ok(ApiResponse.success("AI 생성 일정을 성공적으로 조회했습니다.", schedules));
    }
    
    // 일정 검색 (현재 로그인한 사용자)
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Schedule>>> searchMySchedules(@RequestParam String title) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        String currentUserNickname = SecurityUtil.getCurrentUserNickname();
        log.info("내 일정 검색 요청 - 사용자 ID: {}, 닉네임: {}, 검색어: {}", currentUserId, currentUserNickname, title);
        
        if (currentUserId == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("인증이 필요합니다."));
        }
        
        List<Schedule> schedules = scheduleService.searchSchedules(currentUserId, title);
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
    
    // 사용자별 일정 개수 조회 (현재 로그인한 사용자)
    @GetMapping("/my-count")
    public ResponseEntity<ApiResponse<Long>> getMyScheduleCount() {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        String currentUserNickname = SecurityUtil.getCurrentUserNickname();
        log.info("내 일정 개수 조회 요청 - 사용자 ID: {}, 닉네임: {}", currentUserId, currentUserNickname);
        
        if (currentUserId == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("인증이 필요합니다."));
        }
        
        long count = scheduleService.getScheduleCountByUserId(currentUserId);
        return ResponseEntity.ok(ApiResponse.success("일정 개수를 성공적으로 조회했습니다.", count));
    }
}

