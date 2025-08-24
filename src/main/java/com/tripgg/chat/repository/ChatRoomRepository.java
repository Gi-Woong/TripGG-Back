package com.tripgg.chat.repository;

import com.tripgg.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {
    
    /**
     * 사용자 위치에서 가장 가까운 채팅방 찾기
     * 위도, 경도 범위 내의 활성화된 채팅방을 조회
     */
    @Query("SELECT cr FROM ChatRoom cr WHERE " +
           "cr.isActive = true AND " +
           "cr.latitude BETWEEN :minLat AND :maxLat AND " +
           "cr.longitude BETWEEN :minLng AND :maxLng " +
           "ORDER BY " +
           "SQRT(POWER(cr.latitude - :userLat, 2) + POWER(cr.longitude - :userLng, 2)) ASC")
    List<ChatRoom> findNearestChatRooms(
        @Param("userLat") Double userLat,
        @Param("userLng") Double userLng,
        @Param("minLat") Double minLat,
        @Param("maxLat") Double maxLat,
        @Param("minLng") Double minLng,
        @Param("maxLng") Double maxLng
    );
    
    /**
     * 특정 지역의 채팅방 찾기
     */
    Optional<ChatRoom> findByLocationNameAndIsActiveTrue(String locationName);
    
    /**
     * 활성화된 모든 채팅방 조회
     */
    List<ChatRoom> findByIsActiveTrue();
    
    /**
     * 특정 좌표에서 반경 내 채팅방 찾기
     */
    @Query("SELECT cr FROM ChatRoom cr WHERE " +
           "cr.isActive = true AND " +
           "SQRT(POWER(cr.latitude - :latitude, 2) + POWER(cr.longitude - :longitude, 2)) <= :radiusKm")
    List<ChatRoom> findChatRoomsWithinRadius(
        @Param("latitude") Double latitude,
        @Param("longitude") Double longitude,
        @Param("radiusKm") Double radiusKm
    );
}
