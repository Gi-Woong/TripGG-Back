package com.tripgg.place.controller;

import com.tripgg.common.dto.ApiResponse;
import com.tripgg.place.entity.Place;
import com.tripgg.place.service.PlaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/places")
@RequiredArgsConstructor
public class PlaceController {
    
    private final PlaceService placeService;
    
    // 장소 생성
    @PostMapping
    public ResponseEntity<ApiResponse<Place>> createPlace(@RequestBody Place place) {
        Place createdPlace = placeService.createPlace(place);
        return ResponseEntity.ok(ApiResponse.success("장소가 성공적으로 생성되었습니다.", createdPlace));
    }
    
    // 장소 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Place>> getPlaceById(@PathVariable Long id) {
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
}

