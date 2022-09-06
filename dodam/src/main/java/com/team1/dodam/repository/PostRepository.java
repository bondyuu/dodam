package com.team1.dodam.repository;

import com.querydsl.core.types.dsl.StringExpression;
import com.team1.dodam.domain.Post;
import com.team1.dodam.domain.QPost;
import com.team1.dodam.shared.Category;
import com.team1.dodam.shared.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

public interface PostRepository extends JpaRepository<Post, Long>,
                                        QuerydslPredicateExecutor<Post>,
                                        QuerydslBinderCustomizer<QPost> {
    @Override
    default void customize(QuerydslBindings bindings, QPost root) {
        bindings.excludeUnlistedProperties(true);
//        bindings.including(root.title, root.content, root.createdAt);
        bindings.including(root.title, root.content);
        bindings.bind(root.title).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.content).first(StringExpression::containsIgnoreCase);
//        bindings.bind(root.createdAt).first(DateTimeExpression::eq);
    }

//    @Query(value = "SELECT * FROM Post post WHERE post.post_status = 'CREATED' or post.post_status = 'MODIFIED'", nativeQuery = true)
    Slice<Post> findAllByPostStatus(PostStatus activated, Pageable pageable);

//    Slice<Post> findByTitleContainingAndCategoryAndPostStatus(String title, Category category, PostStatus activated, Pageable pageable);
    Slice<Post> findTop6ByTitleContainingAndCategoryAndPostStatus(String title, Category category, PostStatus activated, Pageable pageable);
//    Slice<Post> findByTitleContainingAndPostStatus(String searchValue, PostStatus activated, Pageable pageable);
    Slice<Post> findTop6ByTitleContainingAndPostStatus(String searchValue, PostStatus activated, Pageable pageable);
//    Page<Post> findByDeviceContaining(String searchValue, Pageable pageable);
//
//    Page<Post> findByApplianceContaining(String searchValue, Pageable pageable);
//
//    Page<Post> findByKitchenContaining(String searchValue, Pageable pageable);
//
//    Page<Post> findByWomenContaining(String searchValue, Pageable pageable);
//
//    Page<Post> findByMenContaining(String searchValue, Pageable pageable);
//
//    Page<Post> findByBeautyContaining(String searchValue, Pageable pageable);
//
//    Page<Post> findByGameContaining(String searchValue, Pageable pageable);
//
//    Page<Post> findByBookContaining(String searchValue, Pageable pageable);
//
//    Page<Post> findByTicketContaining(String searchValue, Pageable pageable);
}
