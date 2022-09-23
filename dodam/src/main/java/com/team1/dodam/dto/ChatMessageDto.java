package com.team1.dodam.dto;

import com.team1.dodam.domain.ChatMessage;
import com.team1.dodam.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageDto {

    private ChatMessage.MessageType type;
    private String sender;
    private String message;
    private LocalDateTime createdAt;
}
