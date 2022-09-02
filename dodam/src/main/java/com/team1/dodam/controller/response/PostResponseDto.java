package com.team1.dodam.controller.response;

import com.team1.dodam.domain.Post;
import com.team1.dodam.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private List<String> imageUrl;
    private String nickname;
    private String userImageUrl;

    @Builder
    public PostResponseDto(Post post, User user, List<String> imageUrlList) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.category = post.getCategory();
        this.postStatus = String.valueOf(post.getPostStatus());
        this.imageUrl = imageUrlList;
        this.nickname = user.getNickname();
        this.userImageUrl = user.getProfileUrl();
    }
}
