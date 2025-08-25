package com.tripgg.place.service;

import com.tripgg.place.dto.KakaoPlaceResponse;
import com.tripgg.place.dto.PlaceSearchRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoMapService {
    
    private final RestTemplate restTemplate;
    
    @Value("${kakao.rest-api-key}")
    private String kakaoApiKey;
    
    @Value("${kakao.map.api.base-url:https://dapi.kakao.com}")
    private String baseUrl;
    
    /**
     * 키워드로 장소 검색
     */
    public KakaoPlaceResponse searchByKeyword(PlaceSearchRequest request) {
        log.info("카카오맵 키워드 검색 시작 - API 키: {}, baseUrl: {}", 
                kakaoApiKey != null ? kakaoApiKey.substring(0, Math.min(10, kakaoApiKey.length())) + "..." : "null", baseUrl);
        
        String url = baseUrl + "/v2/local/search/keyword.json";
        log.info("요청 URL: {}", url);
        
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("query", request.getKeyword());
        
        if (request.getCategory() != null) {
            builder.queryParam("category_group_code", request.getCategory());
        }
        if (request.getX() != null) {
            builder.queryParam("x", request.getX());
        }
        if (request.getY() != null) {
            builder.queryParam("y", request.getY());
        }
        if (request.getRadius() != null) {
            builder.queryParam("radius", request.getRadius());
        }
        if (request.getPage() != null) {
            builder.queryParam("page", request.getPage());
        }
        if (request.getSize() != null) {
            builder.queryParam("size", request.getSize());
        }
        if (request.getSort() != null) {
            builder.queryParam("sort", request.getSort());
        }
        
        String finalUrl = builder.toUriString();
        log.info("최종 요청 URL: {}", finalUrl);
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);
        log.info("Authorization 헤더: KakaoAK {}", 
                kakaoApiKey != null ? kakaoApiKey.substring(0, Math.min(10, kakaoApiKey.length())) + "..." : "null");
        
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        try {
            log.info("카카오맵 API 호출 시작");
            ResponseEntity<KakaoPlaceResponse> response = restTemplate.exchange(
                    finalUrl,
                    HttpMethod.GET,
                    entity,
                    KakaoPlaceResponse.class
            );
            
            log.info("카카오맵 API 응답 상태: {}, 헤더: {}", response.getStatusCode(), response.getHeaders());
            if (response.getBody() != null) {
                log.info("카카오맵 API 응답 본문 - meta: {}, documents: {}개", 
                        response.getBody().getMeta(), 
                        response.getBody().getDocuments() != null ? response.getBody().getDocuments().size() : 0);
            }
            
            log.info("카카오맵 API 키워드 검색 성공: {}", request.getKeyword());
            return response.getBody();
            
        } catch (Exception e) {
            log.error("카카오맵 API 키워드 검색 실패: {}", e.getMessage(), e);
            log.warn("API 호출 실패로 더미 데이터 반환");
            return createDummyResponse(request.getKeyword());
        }
    }
    
    /**
     * 카테고리로 장소 검색
     */
    public KakaoPlaceResponse searchByCategory(PlaceSearchRequest request) {
        String url = baseUrl + "/v2/local/search/category.json";
        
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("category_group_code", request.getCategory());
        
        if (request.getX() != null) {
            builder.queryParam("x", request.getX());
        }
        if (request.getY() != null) {
            builder.queryParam("y", request.getY());
        }
        if (request.getRadius() != null) {
            builder.queryParam("radius", request.getRadius());
        }
        if (request.getPage() != null) {
            builder.queryParam("page", request.getPage());
        }
        if (request.getSize() != null) {
            builder.queryParam("size", request.getSize());
        }
        if (request.getSort() != null) {
            builder.queryParam("sort", request.getSort());
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);
        
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<KakaoPlaceResponse> response = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.GET,
                    entity,
                    KakaoPlaceResponse.class
            );
            
            log.info("카카오맵 API 카테고리 검색 성공: {}", request.getCategory());
            return response.getBody();
            
        } catch (Exception e) {
            log.error("카카오맵 API 카테고리 검색 실패: {}", e.getMessage(), e);
            log.warn("API 호출 실패로 더미 데이터 반환");
            return createDummyResponse(request.getCategory());
        }
    }
    
    /**
     * 더미 응답 생성 (API 키 문제 해결 전까지 임시 사용)
     */
    private KakaoPlaceResponse createDummyResponse(String keyword) {
        log.info("더미 응답 생성: {}", keyword);
        
        KakaoPlaceResponse response = new KakaoPlaceResponse();
        
        // Meta 정보
        KakaoPlaceResponse.Meta meta = new KakaoPlaceResponse.Meta();
        meta.setTotalCount(3);
        meta.setPageableCount(3);
        meta.setIsEnd(true);
        
        KakaoPlaceResponse.SameName sameName = new KakaoPlaceResponse.SameName();
        sameName.setKeyword(keyword);
        sameName.setRegion(new ArrayList<>());
        sameName.setSelectedRegion("");
        meta.setSameName(sameName);
        
        response.setMeta(meta);
        
        // Document 정보
        List<KakaoPlaceResponse.Document> documents = new ArrayList<>();
        
        for (int i = 1; i <= 3; i++) {
            KakaoPlaceResponse.Document doc = new KakaoPlaceResponse.Document();
            doc.setId("dummy_" + i);
            doc.setPlaceName(keyword + " 더미 장소 " + i);
            doc.setCategoryName("음식점 > 카페");
            doc.setCategoryGroupCode("CE7");
            doc.setCategoryGroupName("카페");
            doc.setPhone("02-1234-567" + i);
            doc.setAddressName("서울시 강남구 더미동 " + i + "번지");
            doc.setRoadAddressName("서울시 강남구 더미로 " + i + "길");
            doc.setX("127.0" + i);
            doc.setY("37.5" + i);
            doc.setPlaceUrl("http://place.map.kakao.com/dummy" + i);
            doc.setDistance("100" + i);
            
            documents.add(doc);
        }
        
        response.setDocuments(documents);
        
        log.info("더미 응답 생성 완료: {}개 문서", documents.size());
        return response;
    }
}
