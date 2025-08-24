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

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {
    
    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    
    /**
     * 사용자 위치에서 가장 가까운 채팅방 찾기
     */
    public ChatRoom findNearestChatRoom(Double userLat, Double userLng) {
        // 위도 1도 = 약 111km, 경도 1도 = 약 111km * cos(위도)
        double searchRadius = 50.0; // 50km 반경에서 검색
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
