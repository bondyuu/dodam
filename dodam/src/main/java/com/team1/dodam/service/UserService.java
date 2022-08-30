package com.team1.dodam.service;

import com.team1.dodam.controller.request.*;
import com.team1.dodam.controller.response.LoginResponseDto;
import com.team1.dodam.controller.response.MessageResponseDto;
import com.team1.dodam.controller.response.ResponseDto;
import com.team1.dodam.domain.User;
import com.team1.dodam.global.error.ErrorCode;
import com.team1.dodam.jwt.TokenProvider;
import com.team1.dodam.repository.UserRepository;
import com.team1.dodam.shared.Authority;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    @Transactional
    public ResponseDto<?> signup(SignupRequestDto requestDto) {
        Authority  authority = null;

        if (isPresentUser(requestDto.getEmail()) != null) {
            return ResponseDto.fail(ErrorCode.DUPLICATED_EMAIL);
        }

        if (isPresentNickname(requestDto.getNickname()) != null) {
            return ResponseDto.fail(ErrorCode.DUPLICATED_NICKNAME);
        }

        if (!requestDto.getPassword().equals(requestDto.getPasswordConfirm())) {
            return ResponseDto.fail(ErrorCode.PASSWORDS_NOT_MATCHED);
        }

        if(requestDto.getAuthority().equals("ROLE_ADMIN")){
            authority = Authority.ROLE_ADMIN;
        }

        if(requestDto.getAuthority().equals("ROLE_GIVE")){
            authority = Authority.ROLE_GIVE;
        }

        if(requestDto.getAuthority().equals("ROLE_TAKE")){
            authority = Authority.ROLE_TAKE;
        }
        User user = User.builder()
                .email(requestDto.getEmail())
                .name(requestDto.getName())
                .birth(requestDto.getBirth())
                .nickname(requestDto.getNickname())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .imageUrl("")
                .authority(authority)
                .build();

        userRepository.save(user);

        return ResponseDto.success(MessageResponseDto.builder()
                .msg("회원가입 성공")
                .build());
    }

    public ResponseDto<?> login(LoginRequestDto requestDto, HttpServletResponse response) {
        User user = isPresentUser(requestDto.getEmail());
        if (user == null) {
            return ResponseDto.fail(ErrorCode.USER_NOT_FOUND);
        }

        if (!user.validatePassword(passwordEncoder, requestDto.getPassword())) {
            return ResponseDto.fail(ErrorCode.INVALID_USER);
        }

        TokenDto tokenDto = tokenProvider.generateTokenDto(user);
        tokenToHeaders(tokenDto, response);

        return ResponseDto.success(
                LoginResponseDto.builder()
                        .id(user.getId())
                        .birth(user.getBirth())
                        .nickname(user.getNickname())
                        .authority(user.getAuthority().toString())
                        .token(tokenDto)
                        .build());
    }

    public ResponseDto<?> logout(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return ResponseDto.fail(ErrorCode.INVALID_TOKEN);
        }

        User user = tokenProvider.getUserFromAuthentication();
        if (user == null) {
            return ResponseDto.fail(ErrorCode.NOT_LOGIN_STATE);
        }

        return tokenProvider.deleteRefreshToken(user);
    }

    public ResponseDto<?> check(EmailCheckDto emailCheckDto) {
        User user = isPresentUser(emailCheckDto.getEmail());
        if (user == null) {
            return ResponseDto.success(MessageResponseDto.builder().msg("사용가능한 이메일입니다.").build());
        }

        return ResponseDto.fail(ErrorCode.DUPLICATED_EMAIL);
    }


    @Transactional(readOnly = true)
    public User isPresentUser(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        return optionalUser.orElse(null);
    }

    @Transactional(readOnly = true)
    public User isPresentNickname(String nickname) {
        Optional<User> optionalUser = userRepository.findByNickname(nickname);
        return optionalUser.orElse(null);
    }

    public void tokenToHeaders(TokenDto tokenDto, HttpServletResponse response) {
        response.addHeader("Authorization", "Bearer " + tokenDto.getAccessToken());
        response.addHeader("Refresh-Token", tokenDto.getRefreshToken());
        response.addHeader("Access-Token-Expire-Time", tokenDto.getAccessTokenExpiresIn().toString());
    }

}
