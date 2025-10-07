package com.tripgg.home.controller;

import com.tripgg.auth.util.SecurityUtil;
import com.tripgg.common.dto.ApiResponse;
import com.tripgg.schedule.entity.ScheduleItem;
import com.tripgg.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {

    private final ScheduleService scheduleService;

    @GetMapping("/banner")
    public String getBanner() {
        return "redirect:/static/images/banner.jpg";
    }

    @GetMapping("/today-schedule")
    public ResponseEntity<ApiResponse<List<ScheduleItem>>> getTodaySchedule() {

        Long userId = SecurityUtil.getCurrentUserId();
        String userNickname = SecurityUtil.getCurrentUserNickname();

        List<ScheduleItem> todayScheduleItems = scheduleService.getTodayScheduleItem(userId);
        log.info("오늘의 일정 조회 요청 - 사용자 ID: {}, 닉네임: {}" + userId + userNickname);

        return ResponseEntity.ok(ApiResponse.success("오늘의 일정을 성공적으로 조회했습니다.", todayScheduleItems));
    }
}
