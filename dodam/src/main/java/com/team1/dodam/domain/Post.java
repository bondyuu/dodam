package com.team1.dodam.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.team1.dodam.dto.request.PostRequestDto;
import com.team1.dodam.shared.Category;
import com.team1.dodam.shared.DealState;
import com.team1.dodam.shared.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Table(indexes = {
        @Index(columnList = "title"),
        @Index(columnList = "category"),
        @Index(columnList = "createdAt")
})
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
    @Enumerated(EnumType.STRING)
    Category category;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    PostStatus postStatus;

    @Column(nullable = false)
    private int postVisitCount; // 게시글 방문자 수

    @Column(nullable = false)
    private int postPickCount; // 게시글 찜 수

    @Column
    private DealState dealState; // 거래상태

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
//    @JsonManagedReference
    @JsonBackReference(value = "user-post")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_user_post"))
    private User user;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
//    @JsonBackReference
    @JsonManagedReference(value = "post-image")
    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "post",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Image> imageList = new ArrayList<>();

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
//    @JsonBackReference
    @JsonManagedReference(value = "post-postpick")
    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "post",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<PostPick> postPickList = new ArrayList<>();

    private Post(User user, String title, String content, Category category, PostStatus postStatus, int countPostVisit, int postPickCount) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.category = category;
        this.postStatus = postStatus;
        this.postVisitCount = 0;
        this.postPickCount = 0;
    }

    @Builder
    public Post(PostRequestDto requestDto, User user) {
        this.user = user;
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.category = Category.valueOf(requestDto.getCategory());
        this.dealState = DealState.ONGOING;
        this.postStatus = PostStatus.ACTIVATED;
    }

    public void visit() { this.postVisitCount += 1; }

    public void mapToPostPick(PostPick postPick) { postPickList.add(postPick); }
    public void mapToImage(Image image) { imageList.add(image); }

    public void update(PostRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.category = Category.valueOf(requestDto.getCategory());
    }

    public void updatePostPickCount() { this.postPickCount = this.postPickList.size(); }

    public void discountPostPickCount(PostPick postPick) { this.postPickList.remove(postPick); }

    public void updatePostStatusToDeleted() { this.postStatus = PostStatus.DELETED; }
    public void updateDealState() {
        this.dealState = DealState.COMPLETED;
    }
}
