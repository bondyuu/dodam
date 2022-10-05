package com.team1.dodam.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EditProfileResponseDto {
    private Long id;
    private String profileUrl;
    private String nickname;
}
