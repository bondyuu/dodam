package com.team1.dodam.controller.response;

import com.team1.dodam.controller.request.TokenDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
    private Long id;
    private String birth;
    private String nickname;
    private String authority;
    private TokenDto token;
}
