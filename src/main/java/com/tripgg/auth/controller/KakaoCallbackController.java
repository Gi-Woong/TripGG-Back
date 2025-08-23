package com.tripgg.auth.controller;

import com.tripgg.auth.service.KakaoAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class KakaoCallbackController {

    private final KakaoAuthService kakaoAuthService;

    /**
     * 카카오 로그인 콜백 처리
     * 카카오에서 인가 코드를 받아서 로그인 처리 후 메인 페이지로 리다이렉트
     */
    @GetMapping("/auth/kakao/callback")
    public String handleKakaoCallback(@RequestParam String code) {
        try {
            log.info("카카오 로그인 콜백 받음: code={}", code);
            
            // 인가 코드로 로그인 처리
            Map<String, Object> result = kakaoAuthService.kakaoLogin(code);
            
            // 로그인 성공 시 메인 페이지로 리다이렉트
            // TODO: 실제 메인 페이지 URL로 변경
            return "redirect:/static/login.html?login=success";
            
        } catch (Exception e) {
            log.error("카카오 로그인 콜백 처리 실패: {}", e.getMessage());
            // 로그인 실패 시 에러 페이지로 리다이렉트
            return "redirect:/static/login.html?error=login_failed";
        }
    }
}
