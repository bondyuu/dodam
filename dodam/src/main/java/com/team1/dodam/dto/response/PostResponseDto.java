package com.team1.dodam.dto.response;

import com.team1.dodam.domain.Post;
import com.team1.dodam.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDto {
    private Long id;
    private String title;
    private String content;
    private String category;
    private String postStatus;
    private int postVisitCount;
    private int postPickCount;
    private List<String> imageUrl;
    private String nickname;
    private String userImageUrl;
    private String userLocation;
    private LocalDateTime createdAt;

    @Builder
    public PostResponseDto(Post post, User user, List<String> imageUrlList) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.category = String.valueOf(post.getCategory());
        this.postStatus = String.valueOf(post.getPostStatus());
        this.postVisitCount = post.getPostVisitCount();
        this.postPickCount = post.getPostPickCount();
        this.imageUrl = imageUrlList;
        this.nickname = user.getNickname();
        this.userImageUrl = user.getProfileUrl();
        this.userLocation = user.getLocation();
        this.createdAt = post.getCreatedAt();
    }
}
