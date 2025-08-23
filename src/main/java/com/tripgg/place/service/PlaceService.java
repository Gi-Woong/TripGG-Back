package com.tripgg.place.service;

import com.tripgg.place.entity.Place;
import com.tripgg.place.repository.PlaceRepository;
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
public class PlaceService {
    
    private final PlaceRepository placeRepository;
    
    // 장소 생성
    @Transactional
    public Place createPlace(Place place) {
        return placeRepository.save(place);
    }
    
    // 장소 조회
    public Optional<Place> getPlaceById(Long id) {
        return placeRepository.findById(id);
    }
    
    // 전체 장소 목록 조회
    public List<Place> getAllPlaces() {
        return placeRepository.findAll();
    }
    
    // 카테고리별 장소 조회
    public List<Place> getPlacesByCategory(String category) {
        return placeRepository.findByCategory(category);
    }
    
    // 이름으로 장소 검색
    public List<Place> searchPlacesByName(String name) {
        return placeRepository.findByNameContaining(name);
    }
    
    // 주소로 장소 검색
    public List<Place> searchPlacesByAddress(String address) {
        return placeRepository.findByAddressContaining(address);
    }
    
    // 좌표 범위 내 장소 조회
    public List<Place> getPlacesByCoordinatesRange(
            Double minLat, Double maxLat, Double minLng, Double maxLng) {
        return placeRepository.findByCoordinatesRange(minLat, maxLat, minLng, maxLng);
    }
    
    // 장소 수정
    @Transactional
    public Place updatePlace(Long id, Place placeDetails) {
        Place place = placeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("장소를 찾을 수 없습니다."));
        
        if (placeDetails.getName() != null) {
            place.setName(placeDetails.getName());
        }
        if (placeDetails.getCategory() != null) {
            place.setCategory(placeDetails.getCategory());
        }
        if (placeDetails.getAddress() != null) {
            place.setAddress(placeDetails.getAddress());
        }
        if (placeDetails.getLatitude() != null) {
            place.setLatitude(placeDetails.getLatitude());
        }
        if (placeDetails.getLongitude() != null) {
            place.setLongitude(placeDetails.getLongitude());
        }
        if (placeDetails.getDescription() != null) {
            place.setDescription(placeDetails.getDescription());
        }
        
        return placeRepository.save(place);
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
}

