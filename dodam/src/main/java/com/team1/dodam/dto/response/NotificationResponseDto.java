package com.team1.dodam.dto.response;

import com.team1.dodam.domain.ChatRoom;
import com.team1.dodam.domain.Notification;
import com.team1.dodam.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class NotificationResponseDto {

    private String chatRoomId;
    private String content;
    private Boolean isRead;

    public static NotificationResponseDto from(Notification notification){
        NotificationResponseDto notificationResponseDto = new NotificationResponseDto();
        notificationResponseDto.chatRoomId = notification.getChatRoom().getRoomId();
        notificationResponseDto.content = notification.getContent();
        notificationResponseDto.isRead = notification.getIsRead();
        return notificationResponseDto;
    }
}
