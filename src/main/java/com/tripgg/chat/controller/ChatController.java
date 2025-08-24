package com.tripgg.chat.controller;

import com.tripgg.chat.entity.Chat;
import com.tripgg.chat.entity.ChatRoom;
import com.tripgg.chat.service.ChatService;
import com.tripgg.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
    
    private final ChatService chatService;
    
    /**
     * 채팅방 조회 (roomId와 위경도 매칭 필요)
     * GET /api/chat/{roomId}
     */
    @GetMapping("/{roomId}")
    public ResponseEntity<ApiResponse<ChatRoom>> getChatRoom(
            @PathVariable Integer roomId,
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        
        log.info("채팅방 조회: roomId={}, 위도={}, 경도={}", roomId, latitude, longitude);
        
        try {
            ChatRoom chatRoom = chatService.getChatRoomWithLocationCheck(roomId, latitude, longitude);
            if (chatRoom == null) {
                return ResponseEntity.ok(ApiResponse.success("해당 위치에서 접근할 수 없는 채팅방입니다.", null));
            }
            return ResponseEntity.ok(ApiResponse.success("채팅방을 성공적으로 조회했습니다.", chatRoom));
        } catch (Exception e) {
            log.error("채팅방 조회 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("채팅방 조회에 실패했습니다."));
        }
    }
    
    /**
     * 메시지 조회 (사용자 위치 좌표값으로 채팅방 확인)
     * GET /api/chat/{roomId}/messages
     */
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<ApiResponse<List<Chat>>> getChatRoomMessages(
            @PathVariable Integer roomId,
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        
        log.info("채팅방 메시지 조회: roomId={}, 위도={}, 경도={}", roomId, latitude, longitude);
        
        try {
            List<Chat> chats = chatService.getChatRoomMessagesWithLocationCheck(roomId, latitude, longitude);
            if (chats == null) {
                return ResponseEntity.ok(ApiResponse.success("해당 위치에서 접근할 수 없는 채팅방입니다.", null));
            }
            return ResponseEntity.ok(ApiResponse.success("채팅방 메시지를 성공적으로 조회했습니다.", chats));
        } catch (Exception e) {
            log.error("채팅방 메시지 조회 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("채팅방 메시지 조회에 실패했습니다."));
        }
    }
    
    /**
     * 메시지 전송 (사용자 위치 조회 후 위치 벗어나면 안 보내지도록)
     * POST /api/chat/{roomId}/messages/send
     */
    @PostMapping("/{roomId}/messages/send")
    public ResponseEntity<ApiResponse<Chat>> sendMessage(
            @PathVariable Integer roomId,
            @RequestParam String message,
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        
        log.info("메시지 전송: roomId={}, 위도={}, 경도={}, message={}", roomId, latitude, longitude, message);
        
        try {
            Chat chat = chatService.sendMessageWithLocationCheck(roomId, message, latitude, longitude);
            return ResponseEntity.ok(ApiResponse.success("메시지를 성공적으로 전송했습니다.", chat));
        } catch (Exception e) {
            log.error("메시지 전송 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("메시지 전송에 실패했습니다."));
        }
    }
    
    /**
     * 채팅방 생성 (관리자용) - 테스트용 서울/부산 채팅방
     * POST /api/chat/room/create
     */
    @PostMapping("/room/create")
    public ResponseEntity<ApiResponse<ChatRoom>> createChatRoom(
            @RequestParam String roomName,
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam String locationName,
            @RequestParam Double radiusKm) {
        
        log.info("새 채팅방 생성: {} (위도={}, 경도={}, 지역={})", roomName, latitude, longitude, locationName);
        
        try {
            ChatRoom chatRoom = chatService.createChatRoom(roomName, latitude, longitude, locationName, radiusKm);
            return ResponseEntity.ok(ApiResponse.success("채팅방을 성공적으로 생성했습니다.", chatRoom));
        } catch (Exception e) {
            log.error("채팅방 생성 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("채팅방 생성에 실패했습니다."));
        }
    }
    
    /**
     * 모든 채팅방 목록 조회 (테스트용)
     * GET /api/chat/rooms
     */
    @GetMapping("/rooms")
    public ResponseEntity<ApiResponse<List<ChatRoom>>> getAllChatRooms() {
        log.info("모든 채팅방 조회");
        
        try {
            List<ChatRoom> chatRooms = chatService.getAllActiveChatRooms();
            return ResponseEntity.ok(ApiResponse.success("모든 채팅방을 성공적으로 조회했습니다.", chatRooms));
        } catch (Exception e) {
            log.error("채팅방 목록 조회 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("채팅방 목록 조회에 실패했습니다."));
        }
    }
}
