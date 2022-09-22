package com.team1.dodam.controller;

import com.team1.dodam.domain.UserDetailsImpl;
import com.team1.dodam.dto.request.*;
import com.team1.dodam.dto.response.ResponseDto;
import com.team1.dodam.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
    @PostMapping("/check/email")
    public ResponseDto<?> check(@RequestBody EmailCheckDto emailCheckDto ) { return userService.emailCheck(emailCheckDto); }

    //닉네임 중복 확인
    @ApiOperation(value = "닉네임 중복 확인 메소드")
    @PostMapping("/check/nickname")
    public ResponseDto<?> nicknameCheck(@RequestBody NicknameCheckDto nicknameCheckDto) { return userService.nicknameCheck(nicknameCheckDto); }

    @ApiOperation(value = "회원 정보 수정 메소드")
    @PutMapping
    public ResponseDto<?> editProfile(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                      @RequestPart(value = "imageFile", required = false)MultipartFile imageFile,
                                      @RequestPart(value = "requestDto", required = false) ProfileEditRequestDto requestDto) throws IOException {
        return userService.editProfile(userDetails, imageFile, requestDto);
    }

    @ApiOperation(value = "이메일 인증 메소드")
    @PostMapping("/certification")
    public ResponseDto<?> certifyEmail(@RequestBody CertificationRequestDto requestDto) {
        return userService.certify(requestDto);
    }
}
