package com.tripgg.auth.filter;

import com.tripgg.auth.service.JwtService;
import com.tripgg.user.entity.User;
import com.tripgg.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final Long userId;

        // Authorization 헤더가 없거나 Bearer로 시작하지 않으면 다음 필터로 진행
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // JWT 토큰 추출
            jwt = authHeader.substring(7);
            
            // 토큰에서 사용자 ID 추출
            userId = jwtService.extractUserId(jwt);
            
            // 사용자 ID가 있고, 현재 인증되지 않은 상태라면
            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                // 데이터베이스에서 사용자 정보 조회 (Long)
                User user = userRepository.findById(userId).orElse(null);
                
                // 사용자가 존재하고 토큰이 유효하다면
                if (user != null && jwtService.validateToken(jwt, userId)) {
                    
                    // Spring Security 인증 객체 생성
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        user, null, new ArrayList<>()
                    );
                    
                    // 요청 세부 정보 설정
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // SecurityContext에 인증 정보 설정
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    
                    log.debug("JWT 인증 성공: userId={}, nickname={}", user.getId(), user.getNickname());
                } else {
                    log.warn("JWT 토큰 검증 실패 또는 사용자를 찾을 수 없음: userId={}", userId);
                }
            }
        } catch (Exception e) {
            log.error("JWT 인증 처리 중 오류 발생: {}", e.getMessage());
            // 인증 실패 시 SecurityContext를 비우지 않고 다음 필터로 진행
            // 이렇게 하면 인증이 필요한 엔드포인트에서만 401 에러가 발생
        }

        filterChain.doFilter(request, response);
    }
}
