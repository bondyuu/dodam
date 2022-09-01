package com.team1.dodam.domain;

import com.sun.istack.NotNull;
import com.team1.dodam.controller.request.PostRequestDto;
import com.team1.dodam.shared.Authority;
import com.team1.dodam.shared.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Post extends Timestamped {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    PostStatus postStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_user_post"))
    private User user;

    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "post",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Image> imageList = new ArrayList<>();

    @Builder
    public Post(PostRequestDto requestDto, User user) {
        this.user = user;
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.category = requestDto.getCategory();
        this.postStatus = PostStatus.valueOf(requestDto.getPostStatus());
    }
}
