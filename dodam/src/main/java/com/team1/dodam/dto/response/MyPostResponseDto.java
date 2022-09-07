package com.team1.dodam.dto.response;

import com.team1.dodam.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyPostResponseDto {
    private String imageUrl;
    private String title;
    private String location;
    private String category;
    private String createdAt;
}
