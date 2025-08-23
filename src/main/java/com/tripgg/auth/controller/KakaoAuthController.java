package com.tripgg.auth.controller;

import com.tripgg.auth.service.KakaoAuthService;
import com.tripgg.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class KakaoAuthController {

    private final KakaoAuthService kakaoAuthService;

    /**
     * 카카오 로그인
     * @param authorizationCode 카카오에서 받은 인가 코드
     * @return 로그인 결과 (JWT 토큰 포함)
     */
    @PostMapping("/kakao/login")
    public ApiResponse<Map<String, Object>> kakaoLogin(@RequestParam String authorizationCode) {
        try {
            Map<String, Object> result = kakaoAuthService.kakaoLogin(authorizationCode);
            return ApiResponse.success("카카오 로그인 성공", result);
        } catch (Exception e) {
            log.error("카카오 로그인 실패: {}", e.getMessage());
            return ApiResponse.error("카카오 로그인 실패: " + e.getMessage());
        }
    }

    /**
     * 카카오 로그인 URL 생성
     * @return 카카오 로그인 URL
     */
    @GetMapping("/kakao/url")
    public ApiResponse<String> getKakaoLoginUrl() {
        String loginUrl = kakaoAuthService.getKakaoLoginUrl();
        return ApiResponse.success("카카오 로그인 URL", loginUrl);
    }
}
