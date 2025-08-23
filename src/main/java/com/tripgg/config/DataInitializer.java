package com.tripgg.config;

import com.tripgg.user.entity.User;
import com.tripgg.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// @Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    
    @Override
    public void run(String... args) throws Exception {
        // 테스트 사용자 계정 생성
        if (!userRepository.existsByKakaoId("test_kakao_id_001")) {
            User testUser = User.builder()
                    .kakaoId("test_kakao_id_001")
                    .nickname("테스트 사용자")
                    .profileImageUrl("https://example.com/profile.jpg")
                    .build();
            
            userRepository.save(testUser);
            log.info("테스트 사용자 계정이 생성되었습니다.");
        }
        
        log.info("데이터 초기화가 완료되었습니다.");
    }
}
