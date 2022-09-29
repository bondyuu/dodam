package com.team1.dodam.dto;

import com.team1.dodam.domain.ChatMessage;
import com.team1.dodam.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class ChatMessageDto {

    private ChatMessage.MessageType type;
    private String sender;
    private Long senderId;
    private String message;
    private LocalDateTime createdAt;
}
