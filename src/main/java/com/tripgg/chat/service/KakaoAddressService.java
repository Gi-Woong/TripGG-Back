package com.tripgg.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoAddressService {
    
    private final RestTemplate restTemplate;
    
    @Value("${kakao.rest-api-key}")
    private String kakaoRestApiKey;
    
    private static final String KAKAO_COORD2ADDRESS_URL = "https://dapi.kakao.com/v2/local/geo/coord2address.json";
    
    /**
     * 좌표를 주소로 변환
     * @param longitude 경도 (x)
     * @param latitude 위도 (y)
     * @return region_2depth_name (구 단위)
     */
    public String getRegionFromCoordinates(Double longitude, Double latitude) {
        try {
            String url = String.format("%s?x=%s&y=%s&input_coord=WGS84", 
                KAKAO_COORD2ADDRESS_URL, longitude, latitude);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + kakaoRestApiKey);
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            log.info("카카오 주소 API 호출: 위도={}, 경도={}", latitude, longitude);
            
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                Map<String, Object> meta = (Map<String, Object>) body.get("meta");
                
                if (meta != null && (Integer) meta.get("total_count") > 0) {
                    @SuppressWarnings("unchecked")
                    java.util.List<Map<String, Object>> documents = (java.util.List<Map<String, Object>>) body.get("documents");
                    if (documents != null && !documents.isEmpty()) {
                        Map<String, Object> document = documents.get(0);
                        Map<String, Object> address = (Map<String, Object>) document.get("address");
                        
                        if (address != null) {
                            String region2Depth = (String) address.get("region_2depth_name");
                            log.info("좌표 변환 결과: region_2depth_name={}", region2Depth);
                            return region2Depth;
                        }
                    }
                }
            }
            
            log.warn("카카오 주소 API 응답에서 region_2depth_name을 찾을 수 없음");
            return null;
            
        } catch (Exception e) {
            log.error("카카오 주소 API 호출 실패: {}", e.getMessage());
            return null;
        }
    }
}
