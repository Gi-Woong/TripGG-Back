package com.tripgg.chat.repository;

import com.tripgg.chat.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Integer> {
    
    /**
     * 특정 채팅방의 메시지 조회
     */
    List<Chat> findByChatRoomIdOrderByCreatedAtDesc(Integer chatRoomId);
    
    /**
     * 특정 지역의 채팅방 메시지 조회
     */
    @Query("SELECT c FROM Chat c WHERE c.chatRoom.locationName = :locationName ORDER BY c.createdAt DESC")
    List<Chat> findChatsByLocationName(@Param("locationName") String locationName);
    
    /**
     * 사용자가 참여한 채팅방 조회
     */
    List<Chat> findByUserIdOrderByCreatedAtDesc(Long userId);
}
