package com.team1.dodam.dto.response;

import com.team1.dodam.domain.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponseDto {
    private ChatMessage.MessageType type;
    private Long senderId;
    private String sender;
    private String roomId;
    private String message;
    private LocalDateTime createdAt;
}
