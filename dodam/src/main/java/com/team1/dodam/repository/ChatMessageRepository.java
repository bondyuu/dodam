package com.team1.dodam.repository;

import com.team1.dodam.domain.ChatMessage;
import com.team1.dodam.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage,String> {
    List<ChatMessage> findAllByChatRoom(ChatRoom chatRoom);
}
