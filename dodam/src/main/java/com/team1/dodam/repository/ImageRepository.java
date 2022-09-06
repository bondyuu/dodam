package com.team1.dodam.repository;

import com.team1.dodam.domain.Image;
import com.team1.dodam.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image,Long> {

    List<Image> findAllByPost(Post post);
}
