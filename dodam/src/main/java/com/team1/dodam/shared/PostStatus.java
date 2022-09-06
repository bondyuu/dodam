package com.team1.dodam.shared;

import lombok.Getter;

public enum PostStatus {
    ACTIVATED("게시글 활성 상태"),
    DELETED("게시글 비활성 상태");

    @Getter
    private final String description;

    PostStatus(String description) {
        this.description = description;
    }
}
