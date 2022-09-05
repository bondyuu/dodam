package com.team1.dodam.controller;


import com.team1.dodam.controller.response.ResponseDto;
import com.team1.dodam.domain.UserDetailsImpl;
import com.team1.dodam.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping("/mypage")
    public ResponseDto<?> getMypage(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return myPageService.getMypage(userDetails);
    }

    @GetMapping("/mypage/post")
    public ResponseDto<?> getMyPost(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return myPageService.getMyPost(userDetails);
    }
}
