package com.team1.dodam.service;

import com.team1.dodam.controller.request.*;
import com.team1.dodam.controller.response.EditProfileResponseDto;
import com.team1.dodam.controller.response.LoginResponseDto;
import com.team1.dodam.controller.response.MessageResponseDto;
import com.team1.dodam.controller.response.ResponseDto;
import com.team1.dodam.domain.CertificationNumber;
import com.team1.dodam.domain.User;
import com.team1.dodam.domain.UserDetailsImpl;
import com.team1.dodam.global.error.ErrorCode;
import com.team1.dodam.jwt.TokenProvider;
import com.team1.dodam.repository.CertificationNumberRepository;
import com.team1.dodam.repository.UserRepository;
import com.team1.dodam.s3.S3UploadService;
import com.team1.dodam.shared.Authority;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final CertificationNumberRepository certificationNumberRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final S3UploadService s3UploadService;

    @Transactional
    public ResponseDto<?> signup(SignupRequestDto requestDto) {


        if (isPresentEmail(requestDto.getEmail()) != null) {
            return ResponseDto.fail(ErrorCode.DUPLICATED_EMAIL);
        }

        if (isPresentNickname(requestDto.getNickname()) != null) {
            return ResponseDto.fail(ErrorCode.DUPLICATED_NICKNAME);
        }

        if (!requestDto.getPassword().equals(requestDto.getPasswordConfirm())) {
            return ResponseDto.fail(ErrorCode.PASSWORDS_NOT_MATCHED);
        }


        User user = User.builder()
                .email(requestDto.getEmail())
                .nickname(requestDto.getNickname())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .location(requestDto.getLocation())
                .build();

        userRepository.save(user);

        return ResponseDto.success(MessageResponseDto.builder()
                .msg("회원가입 성공")
                .build());
    }

    public ResponseDto<?> login(LoginRequestDto requestDto, HttpServletResponse response) {
        User user = isPresentEmail(requestDto.getEmail());
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
                        .nickname(user.getNickname())
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

    public ResponseDto<?> emailCheck(EmailCheckDto emailCheckDto) {
        User user = isPresentEmail(emailCheckDto.getEmail());
        if (user == null) {
            return ResponseDto.success(MessageResponseDto.builder().msg("사용가능한 이메일입니다.").build());
        }

        return ResponseDto.fail(ErrorCode.DUPLICATED_EMAIL);
    }

    public ResponseDto<?> editProfile(UserDetailsImpl userDetails,
                                      MultipartFile imageFile,
                                      ProfileEditRequestDto requestDto) throws IOException {
        User loginUser = userDetails.getUser();

        String imageUrl = s3UploadService.s3UploadFile(imageFile,"static/user");
        loginUser.edit(imageUrl, requestDto);

        return ResponseDto.success(EditProfileResponseDto.builder()
                                                         .id(loginUser.getId())
                                                         .profileUrl(loginUser.getProfileUrl())
                                                         .nickname(loginUser.getNickname())
                                                         .build());
    }

    public ResponseDto<?> certify(CertificationRequestDto requestDto) {

        CertificationNumber certification = certificationNumberRepository.findByEmail(requestDto.getEmail())
                .orElseThrow( () -> new IllegalArgumentException("이메일 오류"));

        if (!requestDto.getCertificationNum().equals(certification.getCertificationNumber())) {
            return ResponseDto.fail(ErrorCode.NUMER_NOT_MATCHED);
        }

        return ResponseDto.success(MessageResponseDto.builder().msg("인증되었습니다.").build());
    }

    public ResponseDto<?> nicknameCheck(NicknameCheckDto nicknameCheckDto) {
        User user = isPresentNickname(nicknameCheckDto.getNickname());
        if (user == null) {
            return ResponseDto.success(MessageResponseDto.builder().msg("사용가능한 닉네임입니다.").build());
        }

        return ResponseDto.fail(ErrorCode.DUPLICATED_NICKNAME);
    }

    @Transactional(readOnly = true)
    public User isPresentEmail(String email) {
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
