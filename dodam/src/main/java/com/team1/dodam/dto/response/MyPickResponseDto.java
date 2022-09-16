package com.team1.dodam.dto.response;

import com.team1.dodam.domain.Post;
import com.team1.dodam.service.MyPageService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MyPickResponseDto {
    private Long id;
    private String title;
    private String content;
    private String category;
    private String postStatus;
    private int postVisitCount;
    private int postPickCount;
    private String postImageUrl;
    private Long userId;
    private String userNickname;
    private String userProfileImageUrl;
    private String createdAt;

    @Builder
    public MyPickResponseDto(Post post){
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.category = String.valueOf(post.getCategory());
        this.postStatus = String.valueOf(post.getPostStatus());
        this.postVisitCount = post.getPostVisitCount();
        this.postPickCount = post.getPostPickCount();
        this.postImageUrl = post.getImageList().get(0).getImageUrl();
        this.userId = post.getUser().getId();
        this.userNickname = post.getUser().getNickname();
        this.userProfileImageUrl = post.getUser().getProfileUrl();
        this.createdAt = MyPageService.formatTime(post.getCreatedAt());
    }
}
