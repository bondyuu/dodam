package com.team1.dodam.repository;

import com.team1.dodam.domain.Post;
import com.team1.dodam.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

    Long countAllByUser(User user);
}
