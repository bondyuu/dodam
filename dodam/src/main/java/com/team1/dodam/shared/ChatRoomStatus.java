package com.team1.dodam.shared;

import lombok.Getter;

public enum ChatRoomStatus {
    ACTIVATED("채팅방 활성 상태"),
    DELETED("채팅방 비활성 상태");

    @Getter
    private final String description;

    ChatRoomStatus(String description) {
        this.description = description;
    }
}
