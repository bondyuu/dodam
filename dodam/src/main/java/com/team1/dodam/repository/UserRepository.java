package com.team1.dodam.repository;

import com.team1.dodam.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    List<User> findAllByUsernameContainingIgnoreCase(String username);
}
