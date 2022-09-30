package com.team1.dodam.controller;

import com.team1.dodam.domain.UserDetailsImpl;
import com.team1.dodam.dto.response.ResponseDto;
import com.team1.dodam.jwt.TokenProvider;
import com.team1.dodam.service.ChatRoomService;
import com.team1.dodam.shared.CacheKey;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    private final TokenProvider tokenProvider;

    //채팅방 전체조회
    @GetMapping("/rooms/{userId}")
    @Cacheable(value = CacheKey.CAHTROOM, key = "#userId", unless = "#result == null")
    public ResponseDto<?> room(@PathVariable (name = "userId") Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseDto.success(chatRoomService.findAllRoom(userDetails));
    }

    //채팅방생성
    @PostMapping("/room/{postId}")
    public ResponseDto<?> createRoom(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                     @PathVariable Long postId) {
        return chatRoomService.createChatRoom(userDetails, postId);
    }

    //채팅방 상세정보 조회
    @GetMapping("/room/{roomId}")
    @Cacheable(value = CacheKey.CAHTROOM, key = "#roomId", unless = "#result == null")
    public ResponseDto<?> roomInfo(@PathVariable String roomId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseDto.success(chatRoomService.findRoomById(roomId, userDetails));
    }

}

