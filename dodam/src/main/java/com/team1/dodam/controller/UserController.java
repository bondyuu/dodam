package com.team1.dodam.controller;



import com.team1.dodam.controller.request.IdCheckDto;
import com.team1.dodam.controller.request.LoginRequestDto;
import com.team1.dodam.controller.request.SignupRequestDto;
import com.team1.dodam.controller.response.ResponseDto;
import com.team1.dodam.domain.UserDetailsImpl;
import com.team1.dodam.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //회원가입
    @PostMapping("/api/users/signup")
    public ResponseDto<?> signup(@RequestBody SignupRequestDto requestDto) {

        return userService.signup(requestDto);
    }

    //로그인
    @PostMapping("/api/users/login")
    public ResponseDto<?> login(@RequestBody LoginRequestDto requestDto,
                                HttpServletResponse response) {

        return userService.login(requestDto, response);
    }

    //로그아웃
    @PostMapping("api/users/logout")
    public ResponseDto<?> logout(HttpServletRequest request) {
        return userService.logout(request);
    }


    //아이디 중복 확인
    @PostMapping("api/users")
    public ResponseDto<?> check(@RequestBody IdCheckDto idCheckDto) {
        return userService.check(idCheckDto);
    }

}
