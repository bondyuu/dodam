package com.team1.dodam.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Image extends Timestamped{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String imageUrl;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
//    @JsonManagedReference
    @JsonBackReference(value = "user-image")
    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_user_image"))
    private User user;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
//    @JsonManagedReference
    @JsonBackReference(value = "post-image")
    @ManyToOne
    @JoinColumn(name = "post_id", foreignKey = @ForeignKey(name = "FK_post_image"))
    private Post post;
}
