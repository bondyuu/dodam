//package com.team1.dodam.service;
//
//import com.team1.dodam.domain.RefreshToken;
//import com.team1.dodam.domain.User;
//import com.team1.dodam.dto.TokenDto;
//import com.team1.dodam.dto.request.LoginRequestDto;
//import com.team1.dodam.dto.response.LoginResponseDto;
//import com.team1.dodam.jwt.TokenProvider;
//import com.team1.dodam.repository.RefreshTokenRepository;
//import com.team1.dodam.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class AuthService {
//    private final AuthenticationManagerBuilder authenticationManagerBuilder;
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final TokenProvider tokenProvider;
//    private final RefreshTokenRepository refreshTokenRepository;
//
//
//    public User getUserByToken(String token) {
//
//
//        Authentication authentication = tokenProvider.getAuthentication(token.substring(7));
//        Long user_id = Long.parseLong(authentication.getName());
//        return userRepository.findById(user_id).orElseThrow(() ->
//                new UsernameNotFoundException("회원 정보가 존재하지 않습니다."));
//    }
//
//}