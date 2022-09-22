package com.team1.dodam.dto.request;

import com.team1.dodam.domain.ChatMessage;
import com.team1.dodam.domain.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequestDto {
    private ChatMessage.MessageType type;
    private User sender;
    private String roomId;
    private String message;
    private LocalDateTime createdAt;

}