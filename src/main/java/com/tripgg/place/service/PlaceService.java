package com.tripgg.place.service;

import com.tripgg.place.dto.KakaoPlaceResponse;
import com.tripgg.place.dto.PlaceSearchRequest;
import com.tripgg.place.dto.PlaceSearchResult;
import com.tripgg.place.entity.SchedulePlaces;
import com.tripgg.place.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceService {
    
    private final PlaceRepository placeRepository;
    private final KakaoMapService kakaoMapService;
    
    // 장소 생성
    @Transactional
    public SchedulePlaces createPlace(SchedulePlaces schedulePlaces) {
        return placeRepository.save(schedulePlaces);
    }
    
    // 장소 조회
    public Optional<SchedulePlaces> getPlaceById(Long id) {
        return placeRepository.findById(id);
    }
    
    // 전체 장소 목록 조회
    public List<SchedulePlaces> getAllPlaces() {
        return placeRepository.findAll();
    }
    
    // 카테고리별 장소 조회
    public List<SchedulePlaces> getPlacesByCategory(String category) {
        return placeRepository.findByCategory(category);
    }
    
    // 이름으로 장소 검색
    public List<SchedulePlaces> searchPlacesByName(String name) {
        return placeRepository.findByNameContaining(name);
    }
    
    // 주소로 장소 검색
    public List<SchedulePlaces> searchPlacesByAddress(String address) {
        return placeRepository.findByAddressContaining(address);
    }
    
    // 좌표 범위 내 장소 조회
    public List<SchedulePlaces> getPlacesByCoordinatesRange(
            Double minLat, Double maxLat, Double minLng, Double maxLng) {
        return placeRepository.findByCoordinatesRange(minLat, maxLat, minLng, maxLng);
    }
    
    // 장소 수정
    @Transactional
    public SchedulePlaces updatePlace(Long id, SchedulePlaces schedulePlacesDetails) {
        SchedulePlaces schedulePlaces = placeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("장소를 찾을 수 없습니다."));
        
        if (schedulePlacesDetails.getName() != null) {
            schedulePlaces.setName(schedulePlacesDetails.getName());
        }
        if (schedulePlacesDetails.getCategory() != null) {
            schedulePlaces.setCategory(schedulePlacesDetails.getCategory());
        }
        if (schedulePlacesDetails.getAddress() != null) {
            schedulePlaces.setAddress(schedulePlacesDetails.getAddress());
        }
        if (schedulePlacesDetails.getLatitude() != null) {
            schedulePlaces.setLatitude(schedulePlacesDetails.getLatitude());
        }
        if (schedulePlacesDetails.getLongitude() != null) {
            schedulePlaces.setLongitude(schedulePlacesDetails.getLongitude());
        }
        if (schedulePlacesDetails.getDescription() != null) {
            schedulePlaces.setDescription(schedulePlacesDetails.getDescription());
        }
        
        return placeRepository.save(schedulePlaces);
    }
    
    // 장소 삭제
    @Transactional
    public void deletePlace(Long id) {
        placeRepository.deleteById(id);
    }
    
    // 카테고리별 장소 개수 조회
    public long getPlaceCountByCategory(String category) {
        return placeRepository.countByCategory(category);
    }
    
    /**
     * 카테고리 기반 장소 검색 (카카오맵 API 사용)
     */
    public List<PlaceSearchResult> searchPlaces(PlaceSearchRequest request) {
        List<PlaceSearchResult> results = new ArrayList<>();
        
        log.info("카테고리 기반 장소 검색 시작 - category: {}", request.getCategory());
        
        // 카테고리 검색만 지원
        if (request.getCategory() != null && !request.getCategory().trim().isEmpty()) {
            log.info("카카오맵 카테고리 검색 실행: {}", request.getCategory());
            
            try {
                KakaoPlaceResponse kakaoResponse = kakaoMapService.searchByCategory(request);
                
                if (kakaoResponse != null && kakaoResponse.getDocuments() != null) {
                    log.info("카카오맵 API 응답 성공, 문서 수: {}", kakaoResponse.getDocuments().size());
                    
                    List<PlaceSearchResult> kakaoResults = kakaoResponse.getDocuments().stream()
                            .map(this::convertKakaoDocumentToSearchResult)
                            .collect(Collectors.toList());
                    
                    results.addAll(kakaoResults);
                    log.info("카카오맵 결과 변환 완료, 추가된 결과: {}개", kakaoResults.size());
                } else {
                    log.warn("카카오맵 API 응답이 null이거나 documents가 null");
                }
                
            } catch (Exception e) {
                log.error("카카오맵 API 검색 실패: {}", e.getMessage(), e);
            }
        } else {
            log.info("카테고리가 지정되지 않음");
        }
        
        log.info("최종 검색 결과: {}개", results.size());
        return results;
    }
    
    /**
     * 카카오맵 Document를 PlaceSearchResult로 변환
     */
    private PlaceSearchResult convertKakaoDocumentToSearchResult(KakaoPlaceResponse.Document document) {
        PlaceSearchResult result = new PlaceSearchResult();
        result.setId(document.getId());
        result.setPlaceName(document.getPlaceName());
        result.setCategoryName(document.getCategoryName());
        result.setCategoryGroupCode(document.getCategoryGroupCode());
        result.setCategoryGroupName(document.getCategoryGroupName());
        result.setPhone(document.getPhone());
        result.setAddressName(document.getAddressName());
        result.setRoadAddressName(document.getRoadAddressName());
        result.setPlaceUrl(document.getPlaceUrl());
        result.setDistance(document.getDistance());
        result.setSource("kakao");
        
        // 좌표 변환
        try {
            if (document.getX() != null) {
                result.setLongitude(Double.parseDouble(document.getX()));
            }
            if (document.getY() != null) {
                result.setLatitude(Double.parseDouble(document.getY()));
            }
        } catch (NumberFormatException e) {
            log.warn("좌표 변환 실패: x={}, y={}", document.getX(), document.getY());
        }
        
        return result;
    }
    
    /**
     * DB Place 엔티티를 PlaceSearchResult로 변환
     */
    private List<PlaceSearchResult> convertToSearchResults(List<SchedulePlaces> schedulePlacesList, String source) {
        return schedulePlacesList.stream().map(schedulePlaces -> {
            PlaceSearchResult result = new PlaceSearchResult();
            result.setId(String.valueOf(schedulePlaces.getId()));
            result.setPlaceName(schedulePlaces.getName());
            result.setCategoryName(schedulePlaces.getCategory());
            result.setAddressName(schedulePlaces.getAddress());
            result.setLongitude(schedulePlaces.getLongitude());
            result.setLatitude(schedulePlaces.getLatitude());
            result.setDescription(schedulePlaces.getDescription());
            result.setSource(source);
            return result;
        }).collect(Collectors.toList());
    }
}

