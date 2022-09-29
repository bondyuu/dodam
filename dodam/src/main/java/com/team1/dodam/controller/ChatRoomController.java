package com.team1.dodam.controller;

import com.team1.dodam.domain.UserDetailsImpl;
import com.team1.dodam.dto.response.ResponseDto;
import com.team1.dodam.jwt.TokenProvider;
import com.team1.dodam.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    private final TokenProvider tokenProvider;

    //채팅방 전체조회
    @GetMapping("/rooms")
    public ResponseDto<?> room(@AuthenticationPrincipal UserDetailsImpl userDetails) {
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
    public ResponseDto<?> roomInfo(@PathVariable String roomId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseDto.success(chatRoomService.findRoomById(roomId, userDetails));
    }

//    @GetMapping("/room/enter/{roomId}")
//    public String roomDetail(Model model, @PathVariable String roomId) {
//        model.addAttribute("roomId", roomId);
//        return "/chat/roomdetail";
//    }

    //Redis 채팅방 전체조회
//    @GetMapping("/redis/rooms")
//    public ResponseDto<?> findChatroomAllInRedis(@AuthenticationPrincipal UserDetailsImpl userDetails) {
//        return chatRoomService.findChatroomAllInRedis(userDetails);
//    }
//
//    //Redis 채팅방 상세정보 조회
//    @GetMapping("/redis/room/{roomId}")
//    public ResponseDto<?> findDetailChatroomInRedis(@PathVariable String roomId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
//        return chatRoomService.findDetailChatroomInRedis(roomId, userDetails);
//    }
//
//    // Redis 채팅방 삭제 : Redis에서 해당 채팅방 삭제 및 MySQL에서 해당 채팅방의 Status를 Activated에서 Deleted로 변경
//    @PutMapping("/room/{roomId}")
//    public ResponseDto<?> deleteChatroomInRedis(@PathVariable String roomId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
//        return chatRoomService.deleteChatroomInRedis(roomId, userDetails);
//    }
}

