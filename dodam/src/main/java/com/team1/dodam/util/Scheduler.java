package com.team1.dodam.util;

import com.team1.dodam.domain.Post;
import com.team1.dodam.repository.PostRepository;
import com.team1.dodam.service.PostService;
import com.team1.dodam.shared.PostStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Scheduler {
    private final PostRepository postRepository;
    private final PostService postService;

//     매월 매주 매일 새벽 1시에 Schedule 작업 수행
    @Scheduled(cron = "0 0 1 * * *")
    public void deletePosts() {
        List<Post> postList = postRepository.findAllByPostStatus(PostStatus.DELETED);

        for(Post post : postList) {
            LocalDate from = LocalDate.of(post.getModifiedAt().getYear(), post.getModifiedAt().getMonthValue(), post.getModifiedAt().getDayOfMonth());
            LocalDate to = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonthValue(), LocalDate.now().getDayOfMonth());

            Period period = Period.between(from, to);

            if (period.getDays() >= 7) {
                postService.deletePosts(post.getId());
            }
        }
    }
}
