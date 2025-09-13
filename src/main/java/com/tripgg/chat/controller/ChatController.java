package com.tripgg.chat.controller;

import com.tripgg.auth.util.SecurityUtil;
import com.tripgg.chat.entity.Chat;
import com.tripgg.chat.entity.ChatRoom;
import com.tripgg.chat.service.ChatService;
import com.tripgg.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
    
    private final ChatService chatService;
    
    /**
     * 채팅방 자동 선택 및 리다이렉트 (사용자 위치 기반으로 적절한 roomId 반환 후 리다이렉트)
     * GET /api/chat
     */
    @GetMapping("")
    public ResponseEntity<Void> getChatRooms(
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        
        log.info("채팅방 자동 선택: 위도={}, 경도={}", latitude, longitude);
        
        try {
            // 사용자 위치 기반으로 적절한 채팅방 선택
            Integer selectedRoomId = chatService.selectChatRoomByRegion(latitude, longitude);
            
            if (selectedRoomId != null) {
                log.info("채팅방 선택 완료: roomId={}, 리다이렉트: /api/chat/{}/messages", selectedRoomId, selectedRoomId);
                
                // API 명세에 맞게 /api/chat/{roomId}로 리다이렉트
                return ResponseEntity.status(302)
                        .header("Location", "/api/chat/" + selectedRoomId)
                        .build();
            } else {
                log.warn("근처에 접근 가능한 채팅방이 없음");
                // 에러 페이지로 리다이렉트
                return ResponseEntity.status(302)
                        .header("Location", "/static/error.html?message=근처에 접근 가능한 채팅방이 없습니다")
                        .build();
            }
        } catch (Exception e) {
            log.error("채팅방 자동 선택 실패: {}", e.getMessage());
            // 에러 페이지로 리다이렉트
            return ResponseEntity.status(302)
                    .header("Location", "/static/error.html?message=채팅방 선택에 실패했습니다")
                    .build();
        }
    }
    
    /**
     * 채팅방 조회 (API 명세 준수)
     * GET /api/chat/{roomId}
     */
    @GetMapping("/{roomId}")
    public ResponseEntity<ApiResponse<Object>> getChatRoom(
            @PathVariable Integer roomId) {
        
        log.info("채팅방 조회: roomId={}", roomId);
        
        try {
            // 경기도 지역명으로 채팅방 이름 생성
            String regionName = getGyeonggiRegionName(roomId);
            if (regionName != null) {
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("roomId", roomId);
                responseData.put("roomName", regionName + " 채팅방");
                responseData.put("message", "채팅방에 입장했습니다.");
                
                log.info("채팅방 조회 완료: {} (roomId={})", regionName + " 채팅방", roomId);
                return ResponseEntity.ok(ApiResponse.success("채팅방에 입장했습니다.", responseData));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("채팅방 조회 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("채팅방 조회에 실패했습니다."));
        }
    }
    
    /**
     * roomId로 경기도 지역명 반환
     */
    private String getGyeonggiRegionName(Integer roomId) {
        switch (roomId) {
            case 1: return "수원시";
            case 2: return "성남시";
            case 3: return "고양시";
            case 4: return "용인시";
            case 5: return "부천시";
            case 6: return "안산시";
            case 7: return "안양시";
            case 8: return "평택시";
            case 9: return "화성시";
            case 10: return "남양주시";
            case 11: return "파주시";
            case 12: return "김포시";
            case 13: return "이천시";
            case 14: return "안성시";
            case 15: return "의정부시";
            case 16: return "포천시";
            case 17: return "동두천시";
            case 18: return "광명시";
            case 19: return "군포시";
            case 20: return "양평군";
            case 21: return "양주시";
            case 22: return "구리시";
            case 23: return "오산시";
            case 24: return "하남시";
            case 25: return "광주시";
            case 26: return "연천군";
            case 27: return "여주시";
            case 28: return "가평군";
            default: return null;
        }
    }
    
    /**
     * 메시지 조회 (사용자 위치 좌표값으로 채팅방 확인)
     * GET /api/chat/{roomId}/messages
     */
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<ApiResponse<Object>> getChatRoomMessages(
            @PathVariable Integer roomId,
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        
        log.info("채팅방 메시지 조회: roomId={}, 위도={}, 경도={}", roomId, latitude, longitude);
        
        try {
            List<Chat> chats = chatService.getChatRoomMessagesWithLocationCheck(roomId, latitude, longitude);
            if (chats == null) {
                return ResponseEntity.ok(ApiResponse.success("해당 위치에서 접근할 수 없는 채팅방입니다.", null));
            }
            
            // 지역별 메시지 추가 (동적)
            String locationMessage = "";
            try {
                ChatRoom chatRoom = chatService.getChatRoomById(roomId);
                if (chatRoom != null) {
                    locationMessage = chatRoom.getRoomName() + "입니다";
                    log.info("{}입니다", chatRoom.getRoomName());
                } else {
                    // fallback: 기존 하드코딩된 메시지
                    if (roomId == 1) {
                        locationMessage = "소현 채팅방입니다";
                        log.info("소현 채팅방입니다");
                    } else if (roomId == 2) {
                        locationMessage = "기웅 채팅방입니다";
                        log.info("기웅 채팅방입니다");
                    }
                }
            } catch (Exception e) {
                log.warn("채팅방 정보 조회 실패, 기본 메시지 사용: {}", e.getMessage());
                // fallback: 기존 하드코딩된 메시지
                if (roomId == 1) {
                    locationMessage = "소현 채팅방입니다";
                } else if (roomId == 2) {
                    locationMessage = "기웅 채팅방입니다";
                }
            }
            
            // 응답 데이터에 지역 메시지와 채팅 내용을 포함
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("locationMessage", locationMessage);
            responseData.put("messages", chats);
            responseData.put("roomId", roomId);
            
            return ResponseEntity.ok(ApiResponse.success("채팅방 메시지를 성공적으로 조회했습니다.", responseData));
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
        
        Long currentUserId = SecurityUtil.getCurrentUserId();
        String currentUserNickname = SecurityUtil.getCurrentUserNickname();
        log.info("메시지 전송: roomId={}, 위도={}, 경도={}, message={}, 사용자 ID: {}, 닉네임: {}", 
                roomId, latitude, longitude, message, currentUserId, currentUserNickname);
        
        if (currentUserId == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("인증이 필요합니다."));
        }
        
        try {
            Chat chat = chatService.sendMessageWithLocationCheck(roomId, message, latitude, longitude);
            return ResponseEntity.ok(ApiResponse.success("메시지를 성공적으로 전송했습니다.", chat));
        } catch (Exception e) {
            log.error("메시지 전송 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("메시지 전송에 실패했습니다."));
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
