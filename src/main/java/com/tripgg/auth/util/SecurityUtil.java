package com.tripgg.auth.util;

import com.tripgg.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class SecurityUtil {

    /**
     * 현재 인증된 사용자 정보를 가져옵니다.
     * @return 현재 인증된 사용자, 인증되지 않은 경우 null
     */
    public static User getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication != null && authentication.getPrincipal() instanceof User) {
                return (User) authentication.getPrincipal();
            }
            
            return null;
        } catch (Exception e) {
            log.error("현재 사용자 정보 조회 중 오류 발생: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 현재 인증된 사용자의 ID를 가져옵니다.
     * @return 현재 인증된 사용자의 ID, 인증되지 않은 경우 null
     */
    public static Long getCurrentUserId() {
        User currentUser = getCurrentUser();
        return currentUser != null ? currentUser.getId().longValue() : null;
    }

    /**
     * 현재 인증된 사용자의 카카오 ID를 가져옵니다.
     * @return 현재 인증된 사용자의 카카오 ID, 인증되지 않은 경우 null
     */
    public static String getCurrentUserKakaoId() {
        User currentUser = getCurrentUser();
        return currentUser != null ? currentUser.getKakaoId() : null;
    }

    /**
     * 현재 인증된 사용자의 닉네임을 가져옵니다.
     * @return 현재 인증된 사용자의 닉네임, 인증되지 않은 경우 null
     */
    public static String getCurrentUserNickname() {
        User currentUser = getCurrentUser();
        return currentUser != null ? currentUser.getNickname() : null;
    }

    /**
     * 현재 사용자가 인증되었는지 확인합니다.
     * @return 인증된 경우 true, 그렇지 않은 경우 false
     */
    public static boolean isAuthenticated() {
        return getCurrentUser() != null;
    }
}
