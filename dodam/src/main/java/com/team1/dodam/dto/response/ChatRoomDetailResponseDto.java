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
public class ChatRoomDetailResponseDto {

    private String roomId;
    private Long postId;
    private String postTitle;
    private String postImage;
    private Long userId;
    private String profileUrl;
    private String nickname;
    private List<ChatMessageDto> messageList;

}
