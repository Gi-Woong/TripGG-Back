package com.tripgg.video.service;

import com.tripgg.video.entity.Video;
import com.tripgg.video.repository.VideoRepository;
import com.tripgg.user.entity.User;
import com.tripgg.user.service.UserService;
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
public class VideoService {
    
    private final VideoRepository videoRepository;
    private final UserService userService;
    
    /**
     * 비디오 생성
     */
    @Transactional
    public Video createVideo(Video video, Integer userId) {
        User user = userService.getUserById(userId.longValue())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        video.setUser(user);
        return videoRepository.save(video);
    }
    
    /**
     * 비디오 조회
     */
    public Optional<Video> getVideoById(Integer id) {
        return videoRepository.findById(id);
    }
    
    /**
     * 전체 비디오 목록 조회
     */
    public List<Video> getAllVideos() {
        return videoRepository.findAll();
    }
    
    /**
     * 사용자별 비디오 목록 조회
     */
    public List<Video> getVideosByUserId(Integer userId) {
        return videoRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    /**
     * 제목으로 비디오 검색
     */
    public List<Video> searchVideosByTitle(String title) {
        return videoRepository.findByTitleContaining(title);
    }
    
    /**
     * 태그로 비디오 검색
     */
    public List<Video> searchVideosByTag(String tag) {
        return videoRepository.findByTagContaining(tag);
    }
    
    /**
     * 인기 비디오 조회 (좋아요 기준)
     */
    public List<Video> getPopularVideosByLikes() {
        return videoRepository.findTop10ByOrderByLikesDesc();
    }
    
    /**
     * 인기 비디오 조회 (조회수 기준)
     */
    public List<Video> getPopularVideosByViews() {
        return videoRepository.findTop10ByOrderByViewsDesc();
    }
    
    /**
     * 비디오 수정
     */
    @Transactional
    public Video updateVideo(Integer id, Video videoDetails) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("비디오를 찾을 수 없습니다."));
        
        if (videoDetails.getTitle() != null) {
            video.setTitle(videoDetails.getTitle());
        }
        if (videoDetails.getDescription() != null) {
            video.setDescription(videoDetails.getDescription());
        }
        if (videoDetails.getTags() != null) {
            video.setTags(videoDetails.getTags());
        }
        if (videoDetails.getVideoUrl() != null) {
            video.setVideoUrl(videoDetails.getVideoUrl());
        }
        
        return videoRepository.save(video);
    }
    
    /**
     * 비디오 삭제
     */
    @Transactional
    public void deleteVideo(Integer id, Integer userId) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("비디오를 찾을 수 없습니다."));
        
        // 비디오 소유자 확인
        if (!video.getUser().getId().equals(userId)) {
            throw new RuntimeException("비디오를 삭제할 권한이 없습니다.");
        }
        
        videoRepository.delete(video);
        log.info("비디오가 삭제되었습니다. ID: {}, 사용자 ID: {}", id, userId);
    }
    
    /**
     * 좋아요 증가
     */
    @Transactional
    public Video incrementLikes(Integer id) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("비디오를 찾을 수 없습니다."));
        
        video.setLikes(video.getLikes() + 1);
        return videoRepository.save(video);
    }
    
    /**
     * 조회수 증가
     */
    @Transactional
    public Video incrementViews(Integer id) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("비디오를 찾을 수 없습니다."));
        
        video.setViews(video.getViews() + 1);
        return videoRepository.save(video);
    }
}
