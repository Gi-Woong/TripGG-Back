package com.tripgg.schedule.service;

import com.tripgg.schedule.dto.AiScheduleResponse;
import com.tripgg.schedule.entity.Schedule;
import com.tripgg.schedule.entity.ScheduleItem;
import com.tripgg.schedule.repository.ScheduleItemRepository;
import com.tripgg.schedule.repository.ScheduleRepository;
import com.tripgg.schedule.repository.ScheduleItemRepository;
import com.tripgg.place.entity.SchedulePlaces;
import com.tripgg.place.service.PlaceService;
import com.tripgg.user.entity.User;
import com.tripgg.user.repository.UserRepository;
import com.tripgg.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleItemRepository scheduleItemRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Transactional
    public List<ScheduleItem> getTodayScheduleItem(Long userId) {
        return scheduleItemRepository.findTodayStartScheduleItems(userId);
    }

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
    
    // 전체 일정 조회
    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
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
    
    // AI 일정을 DB에 저장
    @Transactional
    public AiScheduleResponse saveAiSchedule(AiScheduleResponse aiResponse, Long userId) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        // Schedule 저장
        Schedule schedule = Schedule.builder()
                .user(user)
                .title(aiResponse.getSchedule().getTitle())
                .description(aiResponse.getSchedule().getDescription())
                .isAiGenerated(true)
                .startDate(parseDateTime(aiResponse.getSchedule().getStartDate()))
                .endDate(parseDateTime(aiResponse.getSchedule().getEndDate()))
                .build();
        
        Schedule savedSchedule = scheduleRepository.save(schedule);
        log.info("AI 일정 저장 완료: scheduleId={}, title={}", savedSchedule.getId(), savedSchedule.getTitle());
        
        // ScheduleItems 저장
        for (var itemDto : aiResponse.getScheduleItems()) {
            ScheduleItem scheduleItem = ScheduleItem.builder()
                    .schedule(savedSchedule)
                    .schedulePlaces(null) // 일단 null로 설정
                    .day(itemDto.getDay())
                    .orderInDay(itemDto.getOrderInDay())
                    .memo(itemDto.getMemo())
                    .startDate(parseDateTime(itemDto.getStartDate()))
                    .endDate(savedSchedule.getEndDate()) // schedules의 end_date를 그대로 사용
                    .startTime(parseTime(itemDto.getStartTime()))
                    .endTime(parseTime(itemDto.getEndTime()))
                    .createdAt(LocalDateTime.now()) // created_at 추가
                    .build();
            
            ScheduleItem savedItem = scheduleItemRepository.save(scheduleItem);
            
            log.info("일정 아이템 저장 완료: id={}, day={}, order={}, memo={}, created_at={}, end_date={}", 
                    savedItem.getId(), itemDto.getDay(), itemDto.getOrderInDay(), itemDto.getMemo(), 
                    savedItem.getCreatedAt(), savedItem.getEndDate());
        }
        
        return aiResponse;
    }
    
    // SchedulePlaces 저장 또는 조회
    private SchedulePlaces saveOrGetPlace(com.tripgg.schedule.dto.PlaceDto placeDto) {
        // 같은 이름과 주소의 장소가 있는지 확인
        Optional<SchedulePlaces> existingPlace = placeService.findByNameAndAddress(placeDto.getName(), placeDto.getAddress());
        
        if (existingPlace.isPresent()) {
            return existingPlace.get();
        }
        
        // 새로운 장소 저장
        SchedulePlaces newPlace = SchedulePlaces.builder()
                .name(placeDto.getName())
                .category(placeDto.getCategory())
                .description(placeDto.getDescription())
                .address(placeDto.getAddress())
                .latitude(placeDto.getLatitude())
                .longitude(placeDto.getLongitude())
                .build();
        
        return placeService.savePlace(newPlace);
    }
    
    // 날짜 문자열을 LocalDateTime으로 변환
    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) {
            return null;
        }
        try {
            // ISO 8601 형식 파싱
            return LocalDateTime.parse(dateTimeStr.replace("Z", ""), 
                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
        } catch (Exception e) {
            log.warn("날짜 파싱 실패: {}", dateTimeStr);
            return LocalDateTime.now();
        }
    }
    
    // 시간 문자열을 LocalTime으로 변환
    private java.time.LocalTime parseTime(String timeStr) {
        if (timeStr == null || timeStr.isEmpty()) {
            return null;
        }
        try {
            return java.time.LocalTime.parse(timeStr);
        } catch (Exception e) {
            log.warn("시간 파싱 실패: {}", timeStr);
            return null;
        }
    }
}

