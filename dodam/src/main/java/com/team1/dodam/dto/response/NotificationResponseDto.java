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

    private Long notificationId;
    private String chatRoomId;
    private String content;
    private Boolean isRead;
    private String postImage;

    public static NotificationResponseDto from(Notification notification){
        NotificationResponseDto notificationResponseDto = new NotificationResponseDto();
        notificationResponseDto.notificationId = notification.getId();
        notificationResponseDto.chatRoomId = notification.getChatRoom().getRoomId();
        notificationResponseDto.content = notification.getContent();
        notificationResponseDto.isRead = notification.getIsRead();
        notificationResponseDto.postImage = notification.getChatRoom().getPost().getImageList().get(0).getImageUrl();
        return notificationResponseDto;
    }
}
