package com.team1.dodam.repository;

import com.team1.dodam.domain.RefreshToken;
import com.team1.dodam.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
  Optional<RefreshToken> findByUser(User user);
}
