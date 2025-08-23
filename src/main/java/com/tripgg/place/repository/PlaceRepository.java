package com.tripgg.place.repository;

import com.tripgg.place.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {
    
    // 카테고리별 장소 조회
    List<Place> findByCategory(String category);
    
    // 이름으로 장소 검색
    List<Place> findByNameContaining(String name);
    
    // 주소로 장소 검색
    List<Place> findByAddressContaining(String address);
    
    // 좌표 범위 내 장소 조회
    @Query("SELECT p FROM Place p WHERE p.latitude BETWEEN :minLat AND :maxLat AND p.longitude BETWEEN :minLng AND :maxLng")
    List<Place> findByCoordinatesRange(
            @Param("minLat") Double minLat,
            @Param("maxLat") Double maxLat,
            @Param("minLng") Double minLng,
            @Param("maxLng") Double maxLng);
    
    // 카테고리별 장소 개수 조회
    long countByCategory(String category);
}

