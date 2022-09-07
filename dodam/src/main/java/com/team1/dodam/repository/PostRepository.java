package com.team1.dodam.repository;

import com.querydsl.core.types.dsl.StringExpression;
import com.team1.dodam.domain.Post;
import com.team1.dodam.domain.QPost;
import com.team1.dodam.shared.Category;
import com.team1.dodam.shared.PostStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import com.team1.dodam.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>,
                                        QuerydslPredicateExecutor<Post>,
                                        QuerydslBinderCustomizer<QPost> {
    @Override
    default void customize(QuerydslBindings bindings, QPost root) {
        bindings.excludeUnlistedProperties(true);
        bindings.including(root.title, root.content);
        bindings.bind(root.title).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.content).first(StringExpression::containsIgnoreCase);
    }

    Slice<Post> findAllByPostStatus(PostStatus activated, Pageable pageable);

    Slice<Post> findTop6ByTitleContainingAndCategoryAndPostStatus(String title, Category category, PostStatus activated, Pageable pageable);

    Slice<Post> findTop6ByTitleContainingAndPostStatus(String searchValue, PostStatus activated, Pageable pageable);

    Long countAllByUser(User user);
    
    List<Post> findAllByUser(User user);

    List<Post> findAllByPostStatus(PostStatus deleted);
}
