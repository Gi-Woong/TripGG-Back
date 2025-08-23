package com.tripgg.user.repository;

import com.tripgg.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByKakaoId(String kakaoId);
    
    boolean existsByKakaoId(String kakaoId);
}
