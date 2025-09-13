package com.tripgg.place.controller;

import com.tripgg.auth.util.SecurityUtil;
import com.tripgg.common.dto.ApiResponse;
import com.tripgg.place.dto.PlaceSearchRequest;
import com.tripgg.place.dto.PlaceSearchResult;
import com.tripgg.place.entity.Place;
import com.tripgg.place.service.PlaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
public class PlaceController {
    
    private final PlaceService placeService;
    
    // 장소 생성
    @PostMapping
    public ResponseEntity<ApiResponse<Place>> createPlace(@RequestBody Place place) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        String currentUserNickname = SecurityUtil.getCurrentUserNickname();
        log.info("장소 생성 요청 - 사용자 ID: {}, 닉네임: {}", currentUserId, currentUserNickname);
        
        Place createdPlace = placeService.createPlace(place);
        return ResponseEntity.ok(ApiResponse.success("장소가 성공적으로 생성되었습니다.", createdPlace));
    }
    
    // 장소 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Place>> getPlaceById(@PathVariable Long id) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        String currentUserNickname = SecurityUtil.getCurrentUserNickname();
        log.info("장소 조회 요청 - 사용자 ID: {}, 닉네임: {}, 장소 ID: {}", currentUserId, currentUserNickname, id);
        
        Place place = placeService.getPlaceById(id)
                .orElseThrow(() -> new RuntimeException("장소를 찾을 수 없습니다."));
        return ResponseEntity.ok(ApiResponse.success("장소를 성공적으로 조회했습니다.", place));
    }
    
    // 전체 장소 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<Place>>> getAllPlaces() {
        List<Place> places = placeService.getAllPlaces();
        return ResponseEntity.ok(ApiResponse.success("전체 장소 목록을 성공적으로 조회했습니다.", places));
    }
    
    // 카테고리별 장소 조회
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<Place>>> getPlacesByCategory(@PathVariable String category) {
        List<Place> places = placeService.getPlacesByCategory(category);
        return ResponseEntity.ok(ApiResponse.success("카테고리별 장소를 성공적으로 조회했습니다.", places));
    }
    
    // 이름으로 장소 검색
    @GetMapping("/search/name")
    public ResponseEntity<ApiResponse<List<Place>>> searchPlacesByName(@RequestParam String name) {
        List<Place> places = placeService.searchPlacesByName(name);
        return ResponseEntity.ok(ApiResponse.success("이름으로 장소 검색이 완료되었습니다.", places));
    }
    
    // 주소로 장소 검색
    @GetMapping("/search/address")
    public ResponseEntity<ApiResponse<List<Place>>> searchPlacesByAddress(@RequestParam String address) {
        List<Place> places = placeService.searchPlacesByAddress(address);
        return ResponseEntity.ok(ApiResponse.success("주소로 장소 검색이 완료되었습니다.", places));
    }
    
    // 좌표 범위 내 장소 조회
    @GetMapping("/search/coordinates")
    public ResponseEntity<ApiResponse<List<Place>>> getPlacesByCoordinatesRange(
            @RequestParam Double minLat,
            @RequestParam Double maxLat,
            @RequestParam Double minLng,
            @RequestParam Double maxLng) {
        List<Place> places = placeService.getPlacesByCoordinatesRange(minLat, maxLat, minLng, maxLng);
        return ResponseEntity.ok(ApiResponse.success("좌표 범위 내 장소를 성공적으로 조회했습니다.", places));
    }
    
    // 장소 수정
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Place>> updatePlace(
            @PathVariable Long id,
            @RequestBody Place placeDetails) {
        Place updatedPlace = placeService.updatePlace(id, placeDetails);
        return ResponseEntity.ok(ApiResponse.success("장소가 성공적으로 수정되었습니다.", updatedPlace));
    }
    
    // 장소 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deletePlace(@PathVariable Long id) {
        placeService.deletePlace(id);
        return ResponseEntity.ok(ApiResponse.success("장소가 성공적으로 삭제되었습니다.", "삭제 완료"));
    }
    
    // 카테고리별 장소 개수 조회
    @GetMapping("/category/{category}/count")
    public ResponseEntity<ApiResponse<Long>> getPlaceCountByCategory(@PathVariable String category) {
        long count = placeService.getPlaceCountByCategory(category);
        return ResponseEntity.ok(ApiResponse.success("카테고리별 장소 개수를 성공적으로 조회했습니다.", count));
    }
    
    /**
     * 카테고리 기반 장소 검색 API
     * 카카오맵 API를 사용하여 카테고리별 장소 검색
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<PlaceSearchResult>>> searchPlaces(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String x,
            @RequestParam(required = false) String y,
            @RequestParam(required = false) Integer radius,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "15") Integer size,
            @RequestParam(required = false, defaultValue = "accuracy") String sort) {
        
        PlaceSearchRequest request = new PlaceSearchRequest();
        request.setCategory(category);
        request.setX(x);
        request.setY(y);
        request.setRadius(radius);
        request.setPage(page);
        request.setSize(size);
        request.setSort(sort);
        
        List<PlaceSearchResult> results = placeService.searchPlaces(request);
        return ResponseEntity.ok(ApiResponse.success("카테고리 기반 장소 검색이 완료되었습니다.", results));
    }
    
    /**
     * 장소 상세 조회 API
     * 카카오맵으로 연결하여 상세정보 반환
     */
    @GetMapping("/detail/{placeId}")
    public ResponseEntity<ApiResponse<PlaceSearchResult>> getPlaceDetail(@PathVariable String placeId) {
        // DB에서 먼저 검색
        try {
            Long id = Long.parseLong(placeId);
            Place place = placeService.getPlaceById(id)
                    .orElseThrow(() -> new RuntimeException("장소를 찾을 수 없습니다."));
            
            PlaceSearchResult result = new PlaceSearchResult();
            result.setId(String.valueOf(place.getId()));
            result.setPlaceName(place.getName());
            result.setCategoryName(place.getCategory());
            result.setAddressName(place.getAddress());
            result.setLongitude(place.getLongitude());
            result.setLatitude(place.getLatitude());
            result.setDescription(place.getDescription());
            result.setSource("database");
            
            return ResponseEntity.ok(ApiResponse.success("장소 상세 정보를 성공적으로 조회했습니다.", result));
            
        } catch (NumberFormatException e) {
            // placeId가 숫자가 아닌 경우 카카오맵에서 검색
            PlaceSearchRequest request = new PlaceSearchRequest();
            request.setKeyword(placeId);
            request.setSize(1);
            
            try {
                List<PlaceSearchResult> results = placeService.searchPlaces(request);
                if (!results.isEmpty()) {
                    return ResponseEntity.ok(ApiResponse.success("장소 상세 정보를 성공적으로 조회했습니다.", results.get(0)));
                } else {
                    return ResponseEntity.ok(ApiResponse.error("장소를 찾을 수 없습니다."));
                }
            } catch (Exception ex) {
                return ResponseEntity.ok(ApiResponse.error("장소 상세 정보 조회에 실패했습니다: " + ex.getMessage()));
            }
        }
    }
}

