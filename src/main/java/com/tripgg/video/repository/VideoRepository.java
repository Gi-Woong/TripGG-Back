package com.tripgg.video.repository;

import com.tripgg.video.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Integer> {
    
    /**
     * 사용자별 비디오 목록 조회
     */
    List<Video> findByUserIdOrderByCreatedAtDesc(Integer userId);
    
    /**
     * 제목으로 비디오 검색
     */
    List<Video> findByTitleContaining(String title);
    
    /**
     * 태그로 비디오 검색
     */
    @Query("SELECT v FROM Video v WHERE v.tags LIKE %:tag%")
    List<Video> findByTagContaining(@Param("tag") String tag);
    
    /**
     * 사용자별 비디오 개수 조회
     */
    long countByUserId(Integer userId);
    
    /**
     * 좋아요 수 기준으로 인기 비디오 조회
     */
    List<Video> findTop10ByOrderByLikesDesc();
    
    /**
     * 조회수 기준으로 인기 비디오 조회
     */
    List<Video> findTop10ByOrderByViewsDesc();
}
