package com.team1.dodam.service;


import com.team1.dodam.dto.response.MyPageResponseDto;
import com.team1.dodam.dto.response.MyPickResponseDto;
import com.team1.dodam.dto.response.MyPostResponseDto;
import com.team1.dodam.dto.response.ResponseDto;
import com.team1.dodam.domain.Post;
import com.team1.dodam.domain.PostPick;
import com.team1.dodam.domain.User;
import com.team1.dodam.domain.UserDetailsImpl;
import com.team1.dodam.repository.ImageRepository;
import com.team1.dodam.repository.PostPickRepository;
import com.team1.dodam.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final PostRepository postRepository;
    private final PostPickRepository postPickRepository;
    private final ImageRepository imageRepository;

    public ResponseDto<?> getMypage(UserDetailsImpl userDetails) {

        User loginUser = userDetails.getUser();

        Long postNum = postRepository.countAllByUser(loginUser);
        Long pickNum = postPickRepository.countAllByUser(loginUser);
        
        return ResponseDto.success(MyPageResponseDto.builder()
                                                    .profileUrl(loginUser.getProfileUrl())
                                                    .nickname(loginUser.getNickname())
                                                    .postNum(postNum)
                                                    .pickNum(pickNum)
                                                    .build());
    }

    public ResponseDto<?> getMyPost(UserDetailsImpl userDetails) {

        User loginUser = userDetails.getUser();

        List<Post> postList = postRepository.findAllByUser(loginUser);

        //stream하는 과정을 method로 빼서 코드 가독성 향상할 필요 있어보임
        List<MyPostResponseDto> postDtoList = postList.stream()
                                                      .map(post -> MyPostResponseDto.builder()
                                                                                    .imageUrl(imageRepository.findAllByPost(post).get(0).getImageUrl())
                                                                                    .location(post.getUser().getLocation())
                                                                                    .title(post.getTitle())
                                                                                    .category(String.valueOf(post.getCategory()))
                                                                                    .createdAt(formatTime(post.getCreatedAt()))
                                                                                    .build())
                                                      .collect(Collectors.toList());
        return ResponseDto.success(postDtoList);
    }

    @Transactional
    public ResponseDto<?> getMyPick(UserDetailsImpl userDetails) {
        User loginUser = userDetails.getUser();
        List<PostPick> postPickList = postPickRepository.findAllByUser(loginUser);

        List<Post> postList = new ArrayList<>();
        for (PostPick pick : postPickList) {
            postList.add(pick.getPost());
        }
        List<MyPickResponseDto> postDtoList = postList.stream()
                .map(post -> MyPickResponseDto.builder()
                        .imageUrl(imageRepository.findAllByPost(post).get(0).getImageUrl())
                        .location(post.getUser().getLocation())
                        .title(post.getTitle())
                        .category(String.valueOf(post.getCategory()))
                        .createdAt(formatTime(post.getCreatedAt()))
                        .build())
                .collect(Collectors.toList());
        return ResponseDto.success(postDtoList);
    }


    private static final int SEC = 60;
    private static final int MIN = 60;
    private static final int HOUR = 24;
    private static final int DAY = 30;
    private static final int MONTH = 12;

    private static String formatTime(LocalDateTime time) {
        String message;

        long seconds = Duration.between(time, LocalDateTime.now()).getSeconds();

        if (seconds < SEC) { message = seconds + "초 전"; }
        else if (seconds / SEC < MIN) { message = seconds / SEC + "분 전"; }
        else if (seconds / (SEC * MIN) < HOUR) { message = seconds / (SEC * MIN) + "시간 전"; }
        else if (seconds / (SEC * MIN * HOUR) < DAY) { message = seconds / (SEC * MIN * HOUR) + "일 전"; }
        else if (seconds / (SEC * MIN * HOUR * MONTH) < MONTH) { message = seconds / (SEC * MIN * HOUR * MONTH) + "개월 전"; }
        else { message = seconds / (SEC * MIN * HOUR * MONTH) + "년 전"; }

        return message;
    }
}
