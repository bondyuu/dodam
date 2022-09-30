package com.team1.dodam.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Access;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collector;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ChatRoomListResponseDto implements Serializable {

    private String roomId;
    private String profileUrl;
    private String nickname;
    private String location;
    private String lastMessage;
    private String lastTime;
    private String postImage;
    private String dealState;

//    private String roomId;
//    private String roomName;
//    private String dealerImageUrl;
//    private String dealerNickname;
//    private String dealerLocation;
//    private String itemImageUrl;
//    private String lastMessage;
//    private String modifiedAt;
}
