package com.team1.dodam.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.team1.dodam.domain.User;
import lombok.*;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserDto {
    private Long id;
    private String email;
    private String nickname;
    private String password;
    private String location;
    private String profileUrl;
    private String createdAt;
    private String modifiedAt;

    public static UserDto of(Long id, String email, String nickname, String password, String location, String profileUrl) {
        return new UserDto(id, email, nickname, password, location, profileUrl, null, null);
    }

    public static UserDto of(Long id, String email, String nickname, String password, String location, String profileUrl, String createdAt, String modifiedAt) {
        return new UserDto(id, email, nickname, password, location, profileUrl, createdAt, modifiedAt);
    }

    public static UserDto from(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setNickname(user.getNickname());
        userDto.setPassword(user.getPassword());
        userDto.setLocation(user.getLocation());
        userDto.setProfileUrl(user.getProfileUrl());
        userDto.setCreatedAt(String.valueOf(user.getCreatedAt()));
        userDto.setModifiedAt(String.valueOf(user.getModifiedAt()));

        return userDto;
    }
}
