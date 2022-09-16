package com.team1.dodam.controller;


import com.team1.dodam.domain.UserDetailsImpl;
import com.team1.dodam.dto.response.ResponseDto;
import com.team1.dodam.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    @GetMapping("/mypage/posts")
    public ResponseDto<?> getMyPost(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                    @PageableDefault(size = 6, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return myPageService.getMyPost(userDetails, pageable);
    }

    @GetMapping("/mypage/picks")
    public ResponseDto<?> getMyPick(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return myPageService.getMyPick(userDetails);
    }
}
