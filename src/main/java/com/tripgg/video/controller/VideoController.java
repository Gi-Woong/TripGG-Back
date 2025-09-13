package com.tripgg.video.controller;

import com.tripgg.auth.util.SecurityUtil;
import com.tripgg.common.dto.ApiResponse;
import com.tripgg.video.entity.Video;
import com.tripgg.video.service.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoController {
    
    private final VideoService videoService;
    
    /**
     * 전체 비디오 목록 조회
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Video>>> getAllVideos() {
        try {
            List<Video> videos = videoService.getAllVideos();
            return ResponseEntity.ok(ApiResponse.success("전체 비디오 목록을 조회했습니다.", videos));
        } catch (Exception e) {
            log.error("전체 비디오 목록 조회 중 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("비디오 목록 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 비디오 상세 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Video>> getVideoById(@PathVariable Integer id) {
        try {
            Video video = videoService.getVideoById(id)
                    .orElse(null);
            
            if (video == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("비디오를 찾을 수 없습니다."));
            }
            
            // 조회수 증가
            videoService.incrementViews(id);
            
            return ResponseEntity.ok(ApiResponse.success("비디오를 조회했습니다.", video));
        } catch (Exception e) {
            log.error("비디오 조회 중 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("비디오 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 사용자별 비디오 목록 조회 (현재 로그인한 사용자)
     */
    @GetMapping("/my-videos")
    public ResponseEntity<ApiResponse<List<Video>>> getMyVideos() {
        try {
            Long currentUserId = SecurityUtil.getCurrentUserId();
            String currentUserNickname = SecurityUtil.getCurrentUserNickname();
            log.info("내 비디오 목록 조회 요청 - 사용자 ID: {}, 닉네임: {}", currentUserId, currentUserNickname);
            
            if (currentUserId == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("인증이 필요합니다."));
            }
            
            List<Video> videos = videoService.getVideosByUserId(currentUserId.intValue());
            return ResponseEntity.ok(ApiResponse.success("사용자별 비디오 목록을 조회했습니다.", videos));
        } catch (Exception e) {
            log.error("사용자별 비디오 목록 조회 중 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("사용자별 비디오 목록 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 제목으로 비디오 검색
     */
    @GetMapping("/search/title")
    public ResponseEntity<ApiResponse<List<Video>>> searchVideosByTitle(@RequestParam String title) {
        try {
            List<Video> videos = videoService.searchVideosByTitle(title);
            return ResponseEntity.ok(ApiResponse.success( "제목으로 비디오를 검색했습니다.", videos));
        } catch (Exception e) {
            log.error("제목으로 비디오 검색 중 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("비디오 검색 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 태그로 비디오 검색
     */
    @GetMapping("/search/tag")
    public ResponseEntity<ApiResponse<List<Video>>> searchVideosByTag(@RequestParam String tag) {
        try {
            List<Video> videos = videoService.searchVideosByTag(tag);
            return ResponseEntity.ok(ApiResponse.success( "태그로 비디오를 검색했습니다.", videos));
        } catch (Exception e) {
            log.error("태그로 비디오 검색 중 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("비디오 검색 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 인기 비디오 조회 (좋아요 기준)
     */
    @GetMapping("/popular/likes")
    public ResponseEntity<ApiResponse<List<Video>>> getPopularVideosByLikes() {
        try {
            List<Video> videos = videoService.getPopularVideosByLikes();
            return ResponseEntity.ok(ApiResponse.success( "인기 비디오를 조회했습니다.", videos));
        } catch (Exception e) {
            log.error("인기 비디오 조회 중 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("인기 비디오 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 인기 비디오 조회 (조회수 기준)
     */
    @GetMapping("/popular/views")
    public ResponseEntity<ApiResponse<List<Video>>> getPopularVideosByViews() {
        try {
            List<Video> videos = videoService.getPopularVideosByViews();
            return ResponseEntity.ok(ApiResponse.success( "인기 비디오를 조회했습니다.", videos));
        } catch (Exception e) {
            log.error("인기 비디오 조회 중 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("인기 비디오 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 비디오 생성
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Video>> createVideo(@RequestBody Video video) {
        try {
            Long currentUserId = SecurityUtil.getCurrentUserId();
            String currentUserNickname = SecurityUtil.getCurrentUserNickname();
            log.info("비디오 생성 요청 - 사용자 ID: {}, 닉네임: {}", currentUserId, currentUserNickname);
            
            if (currentUserId == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("인증이 필요합니다."));
            }
            
            Video createdVideo = videoService.createVideo(video, currentUserId.intValue());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success( "비디오가 생성되었습니다.", createdVideo));
        } catch (Exception e) {
            log.error("비디오 생성 중 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("비디오 생성 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
    
    /**
     * 비디오 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Video>> updateVideo(@PathVariable Integer id, @RequestBody Video videoDetails) {
        try {
            Video updatedVideo = videoService.updateVideo(id, videoDetails);
            return ResponseEntity.ok(ApiResponse.success( "비디오가 수정되었습니다.", updatedVideo));
        } catch (Exception e) {
            log.error("비디오 수정 중 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("비디오 수정 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
    
    /**
     * 비디오 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteVideo(@PathVariable Integer id) {
        try {
            Long currentUserId = SecurityUtil.getCurrentUserId();
            String currentUserNickname = SecurityUtil.getCurrentUserNickname();
            log.info("비디오 삭제 요청 - 사용자 ID: {}, 닉네임: {}, 비디오 ID: {}", currentUserId, currentUserNickname, id);
            
            if (currentUserId == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("인증이 필요합니다."));
            }
            
            videoService.deleteVideo(id, currentUserId.intValue());
            return ResponseEntity.ok(ApiResponse.success("삭제 완료", "비디오가 삭제되었습니다."));
        } catch (RuntimeException e) {
            log.error("비디오 삭제 중 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("비디오 삭제 중 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("비디오 삭제 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 좋아요 증가
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<ApiResponse<Video>> incrementLikes(@PathVariable Integer id) {
        try {
            Video video = videoService.incrementLikes(id);
            return ResponseEntity.ok(ApiResponse.success( "좋아요가 증가했습니다.", video));
        } catch (Exception e) {
            log.error("좋아요 증가 중 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("좋아요 증가 중 오류가 발생했습니다."));
        }
    }
}
