package com.team1.dodam.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Notification {
    private User user;
    private ChatRoom chatRoom;
    private String content;
    private Boolean isRead;
}
