package com.tripgg.auth.service;

import com.tripgg.user.entity.User;
import com.tripgg.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoAuthService {

    private final UserRepository userRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.client-secret}")
    private String clientSecret;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    /**
     * 카카오 로그인 URL 생성 (카카오 공식 방식)
     */
    public String getKakaoLoginUrl() {
        return UriComponentsBuilder
                .fromHttpUrl("https://kauth.kakao.com/oauth/authorize")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .build()
                .toUriString();
    }

    /**
     * 카카오 로그인 처리
     */
    public Map<String, Object> kakaoLogin(String authorizationCode) {
        // 1. 액세스 토큰 받기
        String accessToken = getKakaoAccessToken(authorizationCode);
        
        // 2. 카카오 사용자 정보 받기
        Map<String, Object> userInfo = getKakaoUserInfo(accessToken);
        
        // 3. 사용자 정보로 로그인/회원가입 처리
        User user = processUserLogin(userInfo);
        
        // 4. JWT 토큰 생성 (간단한 예시)
        String jwtToken = generateJwtToken(user);
        
        // 5. 결과 반환
        Map<String, Object> result = new HashMap<>();
        result.put("accessToken", jwtToken);
        result.put("user", user);
        result.put("isNewUser", user.getUpdatedAt() == null);
        
        return result;
    }

    /**
     * 카카오 액세스 토큰 받기
     */
    private String getKakaoAccessToken(String authorizationCode) {
        String tokenUrl = "https://kauth.kakao.com/oauth/token";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        String body = String.format(
            "grant_type=authorization_code&client_id=%s&client_secret=%s&code=%s&redirect_uri=%s",
            clientId, clientSecret, authorizationCode, redirectUri
        );
        
        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);
        
        Map<String, Object> tokenInfo = response.getBody();
        return (String) tokenInfo.get("access_token");
    }

    /**
     * 카카오 사용자 정보 받기
     */
    private Map<String, Object> getKakaoUserInfo(String accessToken) {
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(
            userInfoUrl, HttpMethod.GET, request, Map.class
        );
        
        return response.getBody();
    }

    /**
     * 사용자 로그인/회원가입 처리
     */
    private User processUserLogin(Map<String, Object> kakaoUserInfo) {
        log.info("카카오 사용자 정보: {}", kakaoUserInfo);
        
        // null 체크 및 안전한 데이터 추출
        Map<String, Object> kakaoAccount = (Map<String, Object>) kakaoUserInfo.get("kakao_account");
        if (kakaoAccount == null) {
            log.error("kakao_account가 null입니다. 사용자 정보: {}", kakaoUserInfo);
            throw new RuntimeException("카카오 계정 정보를 가져올 수 없습니다. 동의 항목을 확인해주세요.");
        }
        
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        if (profile == null) {
            log.error("profile이 null입니다. kakao_account: {}", kakaoAccount);
            throw new RuntimeException("카카오 프로필 정보를 가져올 수 없습니다. 동의 항목을 확인해주세요.");
        }
        
        String kakaoId = String.valueOf(kakaoUserInfo.get("id"));
        if (kakaoId == null || "null".equals(kakaoId)) {
            log.error("kakao_id가 null입니다. 사용자 정보: {}", kakaoUserInfo);
            throw new RuntimeException("카카오 ID를 가져올 수 없습니다.");
        }
        
        String nickname = (String) profile.get("nickname");
        String profileImageUrl = (String) profile.get("profile_image_url");
        
        // 기존 사용자 확인
        Optional<User> existingUserOpt = userRepository.findByKakaoId(kakaoId);
        
        if (existingUserOpt.isPresent()) {
            // 기존 사용자 정보 업데이트
            User existingUser = existingUserOpt.get();
            existingUser.setNickname(nickname);
            existingUser.setProfileImageUrl(profileImageUrl);
            return userRepository.save(existingUser);
        } else {
            // 새 사용자 생성
            User newUser = User.builder()
                    .kakaoId(kakaoId)
                    .nickname(nickname)
                    .profileImageUrl(profileImageUrl)
                    .build();
            return userRepository.save(newUser);
        }
    }

    /**
     * JWT 토큰 생성 (간단한 예시)
     */
    private String generateJwtToken(User user) {
        // 실제로는 JWT 서비스를 사용해야 합니다
        return "jwt_token_" + user.getId() + "_" + System.currentTimeMillis();
    }
}
