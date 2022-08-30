package com.team1.dodam.domain;

import com.team1.dodam.controller.request.TalentRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

import static javax.persistence.CascadeType.ALL;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Talent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long talentId;

    @JoinColumn(name = "userId")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column
    private String imageUrl;

    @Column
    private String title;

    @Column
    private String content;

    @Column
    private String date;

    @Column
    private String place;

    @Column
    @OneToMany(mappedBy = "talent", cascade = ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TalentTag> tagList;

    @Builder
    public Talent(TalentRequestDto requestDto, String imageUrl, User user) {
        this.user = user;
        this.imageUrl = imageUrl;
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.date = requestDto.getDate();
        this.place = requestDto.getPlace();
    }

}
