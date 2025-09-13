package com.tripgg.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j
public class JwtService {

    @Value("${jwt.secret:mySecretKey123456789012345678901234567890}")
    private String secretKey;

    @Value("${jwt.expiration:86400000}") // 24시간
    private long jwtExpiration;

    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * JWT 토큰 생성
     */
    public String generateToken(Long userId, String kakaoId, String nickname) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("kakaoId", kakaoId);
        claims.put("nickname", nickname);
        return createToken(claims, String.valueOf(userId));
    }

    /**
     * JWT 토큰 생성 (User 객체 사용)
     */
    public String generateToken(com.tripgg.user.entity.User user) {
        return generateToken(user.getId().longValue(), user.getKakaoId(), user.getNickname());
    }

    /**
     * 토큰 생성
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 토큰에서 사용자 ID 추출
     */
    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", Long.class));
    }

    /**
     * 토큰에서 카카오 ID 추출
     */
    public String extractKakaoId(String token) {
        return extractClaim(token, claims -> claims.get("kakaoId", String.class));
    }

    /**
     * 토큰에서 닉네임 추출
     */
    public String extractNickname(String token) {
        return extractClaim(token, claims -> claims.get("nickname", String.class));
    }

    /**
     * 토큰에서 subject 추출
     */
    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * 토큰에서 만료일 추출
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * 토큰에서 특정 클레임 추출
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 토큰에서 모든 클레임 추출
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 토큰 만료 여부 확인
     */
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * 토큰 유효성 검증
     */
    public Boolean validateToken(String token, Long userId) {
        try {
            final Long extractedUserId = extractUserId(token);
            return (extractedUserId.equals(userId) && !isTokenExpired(token));
        } catch (Exception e) {
            log.error("토큰 검증 실패: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 토큰 유효성 검증 (사용자 ID 없이)
     */
    public Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            log.error("토큰 검증 실패: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Authorization 헤더에서 토큰 추출
     */
    public String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
