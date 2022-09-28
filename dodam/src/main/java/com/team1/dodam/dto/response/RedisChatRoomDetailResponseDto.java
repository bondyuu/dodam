package com.team1.dodam.dto.response;

import com.team1.dodam.dto.ChatMessageDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class RedisChatRoomDetailResponseDto {

    private String roomName;
    private String itemName;
    private String itemImageUrl;
    private String dealerNickname;
    private String dealerImageUrl;
    private List<ChatMessageDto> messageList;
}
