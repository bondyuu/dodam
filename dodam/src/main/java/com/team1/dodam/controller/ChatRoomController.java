package com.team1.dodam.controller;

import com.team1.dodam.domain.ChatRoom;
import com.team1.dodam.domain.UserDetailsImpl;
import com.team1.dodam.dto.response.ResponseDto;
import com.team1.dodam.jwt.TokenProvider;
import com.team1.dodam.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    private final TokenProvider tokenProvider;

    //채팅방 전체조회
    @GetMapping("/rooms")
    public ResponseDto<?> room() {
        return ResponseDto.success(chatRoomService.findAllRoom());
    }

    //채팅방생성
    @PostMapping("/room/{authorId}")
    public ChatRoom createRoom(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long authorId) {
        return chatRoomService.createChatRoom(userDetails, authorId);
    }

    //채팅방 상세정보 조회
    @GetMapping("/room/{roomId}")
    public ChatRoom roomInfo(@PathVariable String roomId) {
        return chatRoomService.findRoomById(roomId);
    }


//    @GetMapping("/room/enter/{roomId}")
//    public String roomDetail(Model model, @PathVariable String roomId) {
//        model.addAttribute("roomId", roomId);
//        return "/chat/roomdetail";
//    }


}

