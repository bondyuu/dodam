package com.team1.dodam.service;

import com.team1.dodam.controller.request.*;
import com.team1.dodam.controller.response.LoginResponseDto;
import com.team1.dodam.controller.response.MessageResponseDto;
import com.team1.dodam.controller.response.ResponseDto;
import com.team1.dodam.domain.User;
import com.team1.dodam.domain.UserDetailsImpl;
import com.team1.dodam.global.error.ErrorCode;
import com.team1.dodam.jwt.TokenProvider;
import com.team1.dodam.repository.UserRepository;
import com.team1.dodam.s3.S3UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final S3UploadService s3UploadService;

    @Transactional
    public ResponseDto<?> signup(SignupRequestDto requestDto) {

        if (null != isPresentUser(requestDto.getUsername())) {
            return ResponseDto.fail(ErrorCode.DUPLICATED_USERNAME);
        }

        if (!requestDto.getPassword().equals(requestDto.getPasswordConfirm())) {
            return ResponseDto.fail(ErrorCode.PASSWORDS_NOT_MATCHED);
        }

        User user = User.builder()
                .username(requestDto.getUsername())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .build();

        userRepository.save(user);

        return ResponseDto.success(MessageResponseDto.builder()
                        .msg("회원가입 성공")
                .build());
    }

    public ResponseDto<?> login(LoginRequestDto requestDto, HttpServletResponse response) {
        User user = isPresentUser(requestDto.getUsername());
        if (null == user) {
            return ResponseDto.fail(ErrorCode.USER_NOT_FOUND);
        }

        if (!user.validatePassword(passwordEncoder, requestDto.getPassword())) {
            return ResponseDto.fail(ErrorCode.INVALID_USER);
        }
        TokenDto tokenDto = tokenProvider.generateTokenDto(user);
        tokenToHeaders(tokenDto, response);

        return ResponseDto.success(
                LoginResponseDto.builder()
                        .userId(user.getUserId())
                        .username(user.getUsername())
                        .token(tokenDto)
                        .build());
    }

    public ResponseDto<?> logout(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return ResponseDto.fail(ErrorCode.INVALID_TOKEN);
        }
        User user = tokenProvider.getUserFromAuthentication();
        if (null == user) {
            return ResponseDto.fail(ErrorCode.NOT_LOGIN_STATE);
        }

        return tokenProvider.deleteRefreshToken(user);
    }

    public ResponseDto<?> check(IdCheckDto idCheckDto) {
        User user = isPresentUser(idCheckDto.getUsername());
        if (null == user) {
            return ResponseDto.success(MessageResponseDto.builder().msg("사용가능한 아이디입니다.").build());
        } else {
            return ResponseDto.fail(ErrorCode.DUPLICATED_USERNAME);
        }
    }


    @Transactional(readOnly = true)
    public User isPresentUser(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        return optionalUser.orElse(null);
    }

    public void tokenToHeaders(TokenDto tokenDto, HttpServletResponse response) {
        response.addHeader("Authorization", "Bearer " + tokenDto.getAccessToken());
        response.addHeader("Refresh-Token", tokenDto.getRefreshToken());
        response.addHeader("Access-Token-Expire-Time", tokenDto.getAccessTokenExpiresIn().toString());
    }

}
