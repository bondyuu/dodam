package com.team1.dodam.controller.response;


import com.team1.dodam.domain.Talent;
import com.team1.dodam.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class TalentResponseDto {
    private Long talentId;
    private String title;
    private String imageUrl;
    private String content;
    private List<String> tagList;
    private String userImageUrl;
    private String nickname;

    @Builder
    public TalentResponseDto(Talent talent, User user, String imageUrl, List<String> tagList) {
        this.talentId = talent.getTalentId();;
        this.title = talent.getTitle();
        this.imageUrl = imageUrl;
        this.content = talent.getContent();
        this.tagList = tagList;
        this.userImageUrl = user.getImageUrl();
        this.nickname = user.getNickname();
    }

}
