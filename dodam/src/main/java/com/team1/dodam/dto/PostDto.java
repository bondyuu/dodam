package com.team1.dodam.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.team1.dodam.domain.Image;
import com.team1.dodam.domain.Post;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class PostDto {
    private Long id;
    private String title;
    private String content;
    private String category;
    private String location;
    private String postStatus;
    private String dealState;
    private int postVisitCount;
    private int postPickCount;
    private String createdAt;
    private String modifiedAt;
    private UserDto userDto;
    private List<Image> images;

    @Builder
    public PostDto(String title, String content, String category, String postStatus, int postVisitCount, int postPickCount, UserDto userDto) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.postStatus = postStatus;
        this.postVisitCount = postVisitCount;
        this.postPickCount = postPickCount;
        this.userDto = userDto;
    }

    public static PostDto from(Post post) {
        PostDto postDto = new PostDto();
        postDto.setId(post.getId());
        postDto.setTitle(post.getTitle());
        postDto.setContent(post.getContent());
        postDto.setCategory(String.valueOf(post.getCategory().getDescription()));
        postDto.setLocation(post.getUser().getLocation());
        postDto.setPostStatus(String.valueOf(post.getPostStatus()));
        postDto.setDealState(post.getDealState().getDescription());
        postDto.setPostVisitCount(post.getPostVisitCount());
        postDto.setPostPickCount(post.getPostPickCount());
        postDto.setCreatedAt(formatTime(post.getCreatedAt()));
        postDto.setModifiedAt(String.valueOf(post.getModifiedAt()));
        postDto.setUserDto(UserDto.from(post.getUser()));
        postDto.setImages(post.getImageList());

        return postDto;
    }

    private static final int SEC = 60;
    private static final int MIN = 60;
    private static final int HOUR = 24;
    private static final int DAY = 30;
    private static final int MONTH = 12;

    private static String formatTime(LocalDateTime time) {
        String message;

        long seconds = Duration.between(time, LocalDateTime.now()).getSeconds();

        if (seconds < SEC) { message = seconds + "초 전"; }
        else if (seconds / SEC < MIN) { message = seconds / SEC + "분 전"; }
        else if (seconds / (SEC * MIN) < HOUR) { message = seconds / (SEC * MIN) + "시간 전"; }
        else if (seconds / (SEC * MIN * HOUR) < DAY) { message = seconds / (SEC * MIN * HOUR) + "일 전"; }
        else if (seconds / (SEC * MIN * HOUR * MONTH) < MONTH) { message = seconds / (SEC * MIN * HOUR * MONTH) + "개월 전"; }
        else { message = seconds / (SEC * MIN * HOUR * MONTH) + "년 전"; }

        return message;
    }

}
