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
                                                                                    .createdAt(post.getCreatedAt())
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
                        .createdAt(post.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
        return ResponseDto.success(postDtoList);
    }
}
