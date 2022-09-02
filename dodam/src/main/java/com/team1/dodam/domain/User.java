package com.team1.dodam.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import com.team1.dodam.controller.request.ProfileEditRequestDto;
import com.team1.dodam.shared.Authority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EntityListeners(AuditingEntityListener.class)
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "users")
public class User extends Timestamped {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private String email;
    

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Column
    private String location;

    @Column
    private String profileUrl;

    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "user",
            orphanRemoval = true)
    private List<Post> postList = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "user",
            orphanRemoval = true)
    private List<Image> imageList = new ArrayList<>();

    public void edit(String imageUrl, ProfileEditRequestDto requestDto) {
        this.profileUrl = imageUrl;
        this.nickname = requestDto.getNickname();
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        User user = (User) o;
        return id != null && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public boolean validatePassword(PasswordEncoder passwordEncoder, String password) {
        return passwordEncoder.matches(password, this.password);
    }
}