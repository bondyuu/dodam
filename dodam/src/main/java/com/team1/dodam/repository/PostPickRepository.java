package com.team1.dodam.repository;

import com.team1.dodam.domain.Post;
import com.team1.dodam.domain.PostPick;
import com.team1.dodam.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostPickRepository extends JpaRepository<PostPick,Long> {
    Optional<PostPick> findByUserAndPost(User loginUser, Post post);
}