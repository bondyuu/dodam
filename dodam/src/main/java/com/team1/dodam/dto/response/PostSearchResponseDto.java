package com.team1.dodam.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.team1.dodam.domain.Post;
import com.team1.dodam.dto.PostDto;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class PostSearchResponseDto {
    private Long id;
    private String title;
    private String content;
    private String category;
    private String postStatus;
    private String location;
    private int postVisitCount;
    private int postPickCount;
    private String postImageUrl;
    private Long userId;
    private String userNickname;
    private String userProfileImageUrl;
    private String createdAt;

    public static PostSearchResponseDto from(PostDto postDto) {
        if (postDto == null) {
            throw new IllegalArgumentException("postDto가 유효하지 않습니다.");
        }
        PostSearchResponseDto postSearchResponseDto = new PostSearchResponseDto();
        postSearchResponseDto.setId(postDto.getId());
        postSearchResponseDto.setTitle(postDto.getTitle());
        postSearchResponseDto.setContent(postDto.getContent());
        postSearchResponseDto.setCategory(postDto.getCategory());
        postSearchResponseDto.setPostStatus(postDto.getPostStatus());
        postSearchResponseDto.setLocation(postDto.getLocation());
        postSearchResponseDto.setPostVisitCount(postDto.getPostVisitCount());
        postSearchResponseDto.setPostPickCount(postDto.getPostPickCount());
        postSearchResponseDto.setPostImageUrl(postDto.getImages().get(0).getImageUrl());
        postSearchResponseDto.setUserId(postDto.getUserDto().getId());
        postSearchResponseDto.setUserNickname(postDto.getUserDto().getNickname());
        postSearchResponseDto.setUserProfileImageUrl(postDto.getUserDto().getProfileUrl());
        postSearchResponseDto.setCreatedAt(postDto.getCreatedAt());

        return postSearchResponseDto;
    }

    public static PostSearchResponseDto from(Post post) {
        return PostSearchResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .category(String.valueOf(post.getCategory()))
                .postStatus(String.valueOf(post.getPostStatus()))
                .postVisitCount(post.getPostVisitCount())
                .postPickCount(post.getPostPickCount())
                .postImageUrl(post.getImageList().get(0).getImageUrl())
                .userId(post.getUser().getId())
                .userNickname(post.getUser().getNickname())
                .userProfileImageUrl(post.getUser().getProfileUrl())
                .createdAt(String.valueOf(post.getCreatedAt()))
                .build();
    }

}
