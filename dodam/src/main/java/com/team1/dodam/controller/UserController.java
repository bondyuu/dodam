package com.team1.dodam.controller;



import com.team1.dodam.controller.request.EmailCheckDto;
import com.team1.dodam.controller.request.LoginRequestDto;
import com.team1.dodam.controller.request.SignupRequestDto;
import com.team1.dodam.controller.response.ResponseDto;
import com.team1.dodam.service.UserService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //회원가입
    @PostMapping("/signup")
    public ResponseDto<?> signup(@RequestBody SignupRequestDto requestDto) { return userService.signup(requestDto); }

    //로그인
    @PostMapping("/login")
    public ResponseDto<?> login(@RequestBody LoginRequestDto requestDto,
                                HttpServletResponse response) { return userService.login(requestDto, response); }

    //로그아웃
    @PostMapping("/logout")
    public ResponseDto<?> logout(HttpServletRequest request) {
        return userService.logout(request);
    }

    //아이디 중복 확인
    @PostMapping
    public ResponseDto<?> check(@RequestBody EmailCheckDto emailCheckDto) { return userService.check(emailCheckDto); }
}
