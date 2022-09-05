package com.team1.dodam.service;


import com.team1.dodam.controller.response.MyPageResponseDto;
import com.team1.dodam.controller.response.ResponseDto;
import com.team1.dodam.domain.PostPick;
import com.team1.dodam.domain.User;
import com.team1.dodam.domain.UserDetailsImpl;
import com.team1.dodam.repository.PostPickRepository;
import com.team1.dodam.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final PostRepository postRepository;
    private final PostPickRepository postPickRepository;

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
}
