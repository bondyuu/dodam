package com.team1.dodam.service;

import com.team1.dodam.domain.ChatRoom;
import com.team1.dodam.domain.User;
import com.team1.dodam.domain.UserDetailsImpl;
import com.team1.dodam.repository.ChatRoomRepository;
import com.team1.dodam.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    // 모든 채팅방 조회
    @Transactional
    public List<ChatRoom> findAllRoom() {
//        return opsHashChatRoom.values(CHAT_ROOMS);
        return chatRoomRepository.findAll();
    }

    // 특정 채팅방 조회
    @Transactional
    public ChatRoom findRoomById(String id) {
//        return opsHashChatRoom.get(CHAT_ROOMS, id);
        return chatRoomRepository.findById(id).orElseThrow(
                ()-> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));
    }

    // 채팅방 생성 : 서버간 채팅방 공유를 위해 redis hash에 저장한다.
    @Transactional
    public ChatRoom createChatRoom(UserDetailsImpl userDetails, Long authorId) {
        User loginUser = userDetails.getUser();
        User postUser = userRepository.findById(authorId).orElseThrow(
                () -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        ChatRoom chatRoom = ChatRoom.create(loginUser, postUser);
//        opsHashChatRoom.put(CHAT_ROOMS, chatRoom.getRoomId(), chatRoom);
        chatRoomRepository.save(chatRoom);
        return chatRoom;
    }
}