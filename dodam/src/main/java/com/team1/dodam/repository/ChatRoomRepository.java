package com.team1.dodam.repository;

import com.team1.dodam.domain.ChatRoom;
import com.team1.dodam.domain.Post;
import com.team1.dodam.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {
    List<ChatRoom> findAllByUser1(User user);
    List<ChatRoom> findAllByuser2(User user);
    Optional<ChatRoom> findByPostAndUser2(Post post,User loginUser);
}
