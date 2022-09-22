package com.team1.dodam.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Access;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ChatRoomListResponseDto {

    private String roomId;
    private String profileUrl;
    private String nickname;
    private String location;
    private String lastMessage;
    private String lastTime;
}
