package com.team1.dodam.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyPickResponseDto {
    private String imageUrl;
    private String title;
    private String location;
    private String category;
    private String createdAt;
}
