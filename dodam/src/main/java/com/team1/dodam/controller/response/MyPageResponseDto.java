package com.team1.dodam.controller.response;


import com.team1.dodam.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class MyPageResponseDto {
    private String profileUrl;
    private String nickname;
    private Long postNum;
    private Long pickNum;
}
