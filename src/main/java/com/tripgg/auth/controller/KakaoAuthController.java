package com.tripgg.auth.controller;

import com.tripgg.auth.service.KakaoAuthService;
import com.tripgg.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class KakaoAuthController {

    private final KakaoAuthService kakaoAuthService;

    /**
     * 카카오 로그인 시작 (GET)
     * 카카오 로그인 URL로 리다이렉트
     */
    @GetMapping("/kakao/login")
    public ResponseEntity<Void> kakaoLoginRedirect() {
        String loginUrl = kakaoAuthService.getKakaoLoginUrl();
        log.info("카카오 로그인 리다이렉트: {}", loginUrl);
        return ResponseEntity.status(302).header("Location", loginUrl).build();
    }

    /**
     * 카카오 로그인
     * @param authorizationCode 카카오에서 받은 인가 코드
     * @return 로그인 결과 (JWT 토큰 포함)
     */
    @PostMapping("/kakao/login")
    @ResponseBody
    public ApiResponse<Map<String, Object>> kakaoLogin(@RequestParam String authorizationCode) {
        try {
            Map<String, Object> result = kakaoAuthService.kakaoLogin(authorizationCode);
            return ApiResponse.success("카카오 로그인 성공", result);
        } catch (Exception e) {
            log.error("카카오 로그인 실패: {}", e.getMessage());
            return ApiResponse.error("카카오 로그인 실패: " + e.getMessage());
        }
    }


}
