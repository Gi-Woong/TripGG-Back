package com.tripgg.chat.service;

import com.tripgg.chat.entity.Chat;
import com.tripgg.chat.entity.ChatRoom;
import com.tripgg.chat.repository.ChatRepository;
import com.tripgg.chat.repository.ChatRoomRepository;
import com.tripgg.user.entity.User;
import com.tripgg.user.repository.UserRepository;
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
public class ChatService {
    
    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final KakaoAddressService kakaoAddressService;
    
    /**
     * 사용자 위치에서 가장 가까운 채팅방 찾기
     */
    public ChatRoom findNearestChatRoom(Double userLat, Double userLng) {
        // 위도 1도 = 약 111km, 경도 1도 = 약 111km * cos(위도)
        double searchRadius = 15.0; // 15km 반경에서 검색
        double latDelta = searchRadius / 111.0;
        double lngDelta = searchRadius / (111.0 * Math.cos(Math.toRadians(userLat)));
        
        double minLat = userLat - latDelta;
        double maxLat = userLat + latDelta;
        double minLng = userLng - lngDelta;
        double maxLng = userLng + lngDelta;
        
        log.info("가장 가까운 채팅방 검색: 위도({}~{}), 경도({}~{})", minLat, maxLat, minLng, maxLng);
        
        List<ChatRoom> nearbyRooms = chatRoomRepository.findNearestChatRooms(
            userLat, userLng, minLat, maxLat, minLng, maxLng
        );
        
        if (nearbyRooms.isEmpty()) {
            log.warn("사용자 위치 근처에 채팅방이 없습니다: 위도={}, 경도={}", userLat, userLng);
            return null;
        }
        
        ChatRoom nearestRoom = nearbyRooms.get(0);
        log.info("가장 가까운 채팅방: {} (위도={}, 경도={})", nearestRoom.getRoomName(), nearestRoom.getLatitude(), nearestRoom.getLongitude());
        
        return nearestRoom;
    }
    
    /**
     * 사용자 위치 기반으로 적절한 채팅방 자동 선택 (경기도 31개 지역 매핑)
     * @param userLat 사용자 위도
     * @param userLng 사용자 경도
     * @return 선택된 채팅방 ID (1~31)
     */
    public Integer selectChatRoomByRegion(Double userLat, Double userLng) {
        log.info("지역 기반 채팅방 선택: 위도={}, 경도={}", userLat, userLng);
        
        try {
            // 카카오 API로 좌표를 주소로 변환
            String region2Depth = kakaoAddressService.getRegionFromCoordinates(userLng, userLat);
            
            if (region2Depth == null) {
                log.warn("좌표를 주소로 변환할 수 없음");
                return null;
            }
            
            log.info("사용자 지역: {}", region2Depth);
            
            // 경기도 31개 지역 매핑
            Integer roomId = getGyeonggiRegionRoomId(region2Depth);
            if (roomId != null) {
                log.info("경기도 지역 매핑 완료: {} → roomId={}", region2Depth, roomId);
                return roomId;
            }
            
            log.warn("경기도 지역이 아님: {}", region2Depth);
            return null;
            
        } catch (Exception e) {
            log.error("지역 기반 채팅방 선택 실패: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 경기도 31개 지역을 roomId로 매핑
     * @param regionName 지역명 (region_2depth_name)
     * @return 채팅방 ID (1~31), 경기도 지역이 아니면 null
     */
    private Integer getGyeonggiRegionRoomId(String regionName) {
        if (regionName == null) return null;
        
        // "수원시 팔달구" → "수원시" 추출
        String extractedRegion = extractMainRegion(regionName);
        if (extractedRegion == null) return null;
        
        log.info("지역명 추출: '{}' → '{}'", regionName, extractedRegion);
        
        // 경기도 31개 지역 매핑
        switch (extractedRegion) {
            case "수원시": return 1;
            case "성남시": return 2;
            case "고양시": return 3;
            case "용인시": return 4;
            case "부천시": return 5;
            case "안산시": return 6;
            case "안양시": return 7;
            case "평택시": return 8;
            case "화성시": return 9;
            case "남양주시": return 10;
            case "파주시": return 11;
            case "김포시": return 12;
            case "이천시": return 13;
            case "안성시": return 14;
            case "의정부시": return 15;
            case "포천시": return 16;
            case "동두천시": return 17;
            case "광명시": return 18;
            case "군포시": return 19;
            case "양평군": return 20;
            case "양주시": return 21;
            case "구리시": return 22;
            case "오산시": return 23;
            case "하남시": return 24;
            case "광주시": return 25;
            case "연천군": return 26;
            case "여주시": return 27;
            case "가평군": return 28;
            default: return null;
        }
    }
    
    /**
     * 지역명에서 시/군/구 추출 (예: "수원시 팔달구" → "수원시")
     * @param fullRegionName 전체 지역명
     * @return 추출된 시/군/구명
     */
    private String extractMainRegion(String fullRegionName) {
        if (fullRegionName == null) return null;
        
        // "시", "군"으로 끝나는 부분 추출
        if (fullRegionName.contains("시")) {
            int endIndex = fullRegionName.indexOf("시") + 1;
            return fullRegionName.substring(0, endIndex);
        } else if (fullRegionName.contains("군")) {
            int endIndex = fullRegionName.indexOf("군") + 1;
            return fullRegionName.substring(0, endIndex);
        }
        
        return fullRegionName; // 원본 반환
    }
    

    
    /**
     * ID로 채팅방 조회
     * @param roomId 채팅방 ID
     * @return 채팅방 정보
     */
    public ChatRoom getChatRoomById(Integer roomId) {
        return chatRoomRepository.findById(roomId).orElse(null);
    }
    

    
    /**
     * 특정 지역의 채팅방 찾기
     */
    public ChatRoom getChatRoomByLocation(String locationName) {
        log.info("지역 채팅방 조회: {}", locationName);
        return chatRoomRepository.findByLocationNameAndIsActiveTrue(locationName)
                .orElse(null);
    }
    
    /**
     * 특정 채팅방의 메시지 조회
     */
    public List<Chat> getChatRoomMessages(Integer chatRoomId) {
        log.info("채팅방 메시지 조회: chatRoomId={}", chatRoomId);
        return chatRepository.findByChatRoomIdOrderByCreatedAtDesc(chatRoomId);
    }
    
    /**
     * 특정 지역의 채팅방 메시지 조회
     */
    public List<Chat> getChatsByLocationName(String locationName) {
        log.info("지역 채팅방 메시지 조회: {}", locationName);
        return chatRepository.findChatsByLocationName(locationName);
    }
    
    /**
     * 사용자가 참여한 채팅방 조회
     */
    public List<Chat> getUserChats(Long userId) {
        log.info("사용자 채팅방 조회: userId={}", userId);
        return chatRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    /**
     * 새 메시지 전송
     */
    @Transactional
    public Chat sendMessage(Long userId, String message, Double latitude, Double longitude) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        // 사용자 위치에서 가장 가까운 채팅방 찾기
        ChatRoom chatRoom = findNearestChatRoom(latitude, longitude);
        if (chatRoom == null) {
            throw new RuntimeException("사용자 위치 근처에 채팅방이 없습니다.");
        }
        
        Chat chat = Chat.builder()
                .user(user)
                .chatRoom(chatRoom)
                .message(message)
                .build();
        
        log.info("새 메시지 전송: userId={}, chatRoom={}, message={}", userId, chatRoom.getRoomName(), message);
        
        return chatRepository.save(chat);
    }
    
    /**
     * 채팅방 생성 (관리자용)
     */
    @Transactional
    public ChatRoom createChatRoom(String roomName, Double latitude, Double longitude, String locationName, Double radiusKm) {
        ChatRoom chatRoom = ChatRoom.builder()
                .roomName(roomName)
                .latitude(latitude)
                .longitude(longitude)
                .locationName(locationName)
                .radiusKm(radiusKm)
                .isActive(true)
                .build();
        
        log.info("새 채팅방 생성: {} (위도={}, 경도={})", roomName, latitude, longitude);
        
        return chatRoomRepository.save(chatRoom);
    }
    
    /**
     * 위치 기반 채팅방 접근 권한 확인
     */
    public ChatRoom getChatRoomWithLocationCheck(Integer roomId, Double userLat, Double userLng) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElse(null);
        if (chatRoom == null || !chatRoom.getIsActive()) {
            return null;
        }
        
        // 사용자 위치가 채팅방 반경 내에 있는지 확인
        double distance = calculateDistance(userLat, userLng, chatRoom.getLatitude(), chatRoom.getLongitude());
        if (distance > chatRoom.getRadiusKm()) {
            log.warn("사용자 위치가 채팅방 반경을 벗어남: 거리={}km, 반경={}km", distance, chatRoom.getRadiusKm());
            return null;
        }
        
        log.info("채팅방 접근 권한 확인 성공: roomId={}, 거리={}km", roomId, distance);
        return chatRoom;
    }
    
    /**
     * 위치 기반 채팅방 메시지 조회 권한 확인
     */
    public List<Chat> getChatRoomMessagesWithLocationCheck(Integer roomId, Double userLat, Double userLng) {
        ChatRoom chatRoom = getChatRoomWithLocationCheck(roomId, userLat, userLng);
        if (chatRoom == null) {
            return null;
        }
        
        return getChatRoomMessages(roomId);
    }
    
    /**
     * 위치 기반 메시지 전송 권한 확인
     */
    @Transactional
    public Chat sendMessageWithLocationCheck(Integer roomId, String message, Double latitude, Double longitude) {
        // 임시로 userId=1 사용 (테스트용)
        Long userId = 1L;
        
        ChatRoom chatRoom = getChatRoomWithLocationCheck(roomId, latitude, longitude);
        if (chatRoom == null) {
            throw new RuntimeException("해당 위치에서 접근할 수 없는 채팅방입니다.");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        Chat chat = Chat.builder()
                .user(user)
                .chatRoom(chatRoom)
                .message(message)
                .build();
        
        log.info("메시지 전송 성공: roomId={}, userId={}, message={}", roomId, userId, message);
        
        return chatRepository.save(chat);
    }
    
    /**
     * 활성화된 모든 채팅방 조회
     */
    public List<ChatRoom> getAllActiveChatRooms() {
        return chatRoomRepository.findByIsActiveTrue();
    }
    
    /**
     * 두 지점 간의 거리 계산 (km)
     */
    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        final double R = 6371; // 지구 반지름 (km)
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lngDistance = Math.toRadians(lng2 - lng1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
}
