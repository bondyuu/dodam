package com.team1.dodam.controller.response;


import com.team1.dodam.domain.Talent;
import com.team1.dodam.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class TalentResponseDto {
    private Long talentId;
    private String title;
    private String imageUrl;
    private String content;
    private String userImageUrl;
    private String nickname;

    @Builder
    public TalentResponseDto(Talent talent, User user) {
        this.talentId = talent.getTalentId();;
        this.title = talent.getTitle();
        this.imageUrl = talent.getImageUrl();
        this.content = talent.getContent();
        this.userImageUrl = user.getImageUrl();
        this.nickname = user.getNickname();
    }

}
