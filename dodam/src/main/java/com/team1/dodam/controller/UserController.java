package com.team1.dodam.controller;

import com.team1.dodam.controller.request.EmailCheckDto;
import com.team1.dodam.controller.request.LoginRequestDto;
import com.team1.dodam.controller.request.NicknameCheckDto;
import com.team1.dodam.controller.request.SignupRequestDto;
import com.team1.dodam.controller.response.ResponseDto;
import com.team1.dodam.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(tags = {"로그인/회원가입/로그아웃/이메일 중복 확인/닉네임 중복 확인"})
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //회원가입
    @ApiOperation(value = "회원가입 메소드")
    @PostMapping("/signup")
    public ResponseDto<?> signup(@RequestBody SignupRequestDto requestDto) { return userService.signup(requestDto); }

    //로그인
    @ApiOperation(value = "로그인 메소드")
    @PostMapping("/login")
    public ResponseDto<?> login(@RequestBody LoginRequestDto requestDto,
                                HttpServletResponse response) { return userService.login(requestDto, response); }

    //로그아웃
    @ApiOperation(value = "로그아웃 메소드")
    @DeleteMapping ("/logout")
    public ResponseDto<?> logout(HttpServletRequest request) {
        return userService.logout(request);
    }

    //이메일 중복 확인
    @ApiOperation(value = "이메일 중복 확인 메소드")
    @PostMapping("/email/check")
    public ResponseDto<?> check(@RequestBody EmailCheckDto emailCheckDto ) { return userService.emailCheck(emailCheckDto); }

    //닉네임 중복 확인
    @ApiOperation(value = "닉네임 중복 확인 메소드")
    @PostMapping("/nickname/check")
    public ResponseDto<?> nicknameCheck(@RequestBody NicknameCheckDto nicknameCheckDto) { return userService.nicknameCheck(nicknameCheckDto); }
}
