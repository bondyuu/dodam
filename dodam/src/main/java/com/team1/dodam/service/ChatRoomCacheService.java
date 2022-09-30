package com.team1.dodam.service;

import com.team1.dodam.domain.*;
import com.team1.dodam.dto.ChatMessageDto;
import com.team1.dodam.dto.response.*;
import com.team1.dodam.global.error.ErrorCode;
import com.team1.dodam.repository.ChatMessageRepository;
import com.team1.dodam.repository.ChatRoomRepository;
import com.team1.dodam.shared.ChatRoomStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatRoomCacheService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Transactional
    public ResponseDto<?> findChatroomAllInRedisOrMySQL(Long userId, UserDetailsImpl userDetails) {

        User loginUser = userDetails.getUser();
        if (loginUser == null || !loginUser.getId().equals(userId)) {
            return ResponseDto.fail(ErrorCode.USER_NOT_FOUND);
        }

        List<ChatRoom> chatRooms = chatRoomRepository.findAll().stream()
                .filter(chatroom -> chatroom.getPostOwnerNickname().equals(loginUser.getNickname()) || chatroom.getRequestDealUserNickname().equals(loginUser.getNickname()))
                .filter(chatRoom -> chatRoom.getChatRoomStatus().equals(ChatRoomStatus.ACTIVATED))
                .collect(Collectors.toList());

        return ResponseDto.success(chatRooms.stream().map(chatRoom -> ChatRoomListResponseDto.builder()
                        .roomId(chatRoom.getRoomId())

                        .profileUrl(chatRoom.getRoomName().split(":")[0].equals(loginUser.getNickname()) ? chatRoom.getRequestDealUserImageUrl() : chatRoom.getPostOwnerImageUrl())
                        .nickname(chatRoom.getRoomName().split(":")[0].equals(loginUser.getNickname()) ? chatRoom.getRequestDealUserNickname() : chatRoom.getPostOwnerNickname())
                        .location(chatRoom.getRoomName().split(":")[0].equals(loginUser.getNickname()) ? chatRoom.getRequestDealUserLocation() : chatRoom.getPostOwnerLocation())
                        .postImage(chatRoom.getItemImageUrl())
                        .lastMessage(Objects.equals(chatMessageRepository.findTopByChatRoom(chatRoom, Sort.by("createdAt")), null) ? "" : chatMessageRepository.findTopByChatRoom(chatRoom, Sort.by("createdAt")).getMessage())
                        .lastTime(MyPageService.formatTime(chatRoom.getModifiedAt()))
                        .build())
                .collect(Collectors.toList()));
    }

    @Transactional
    public ResponseDto<?> findDetailChatroomInRedisOrMySQL(Long userId, String roomId, UserDetailsImpl userDetails) {

        User loginUser = userDetails.getUser();
        if (loginUser == null || !loginUser.getId().equals(userId)) {
            return ResponseDto.fail(ErrorCode.USER_NOT_FOUND);
        }

//        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(()-> new IllegalArgumentException("해당 채팅방을 찾을 수 없습니다."));
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).stream().filter(c -> c.getChatRoomStatus().equals(ChatRoomStatus.ACTIVATED)).findFirst().orElseThrow(()-> new IllegalArgumentException("해당 채팅방을 찾을 수 없습니다."));

        List<ChatMessage> messageList = chatMessageRepository.findAllByChatRoom(chatRoom);

        List<ChatMessageDto> messageDtoList = messageList.stream()
                                                         .map(message -> ChatMessageDto.builder()
                                                                                       .type(message.getType())
                                                                                       .sender(message.getUser().getNickname())
                                                                                       .message(message.getMessage())
                                                                                       .createdAt(message.getCreatedAt())
                                                                                       .build())
                                                         .collect(Collectors.toList());

        return ResponseDto.success(RedisChatRoomDetailResponseDto.builder()
                                                                 .roomName(chatRoom.getRoomName())
                                                                 .itemName(chatRoom.getItemName())
                                                                 .itemImageUrl(chatRoom.getItemImageUrl())
                                                                 .dealerNickname(chatRoom.getRoomName().split(":")[0].equals(loginUser.getNickname()) ? chatRoom.getRequestDealUserNickname() : chatRoom.getPostOwnerNickname())
                                                                 .dealerImageUrl(chatRoom.getRoomName().split(":")[0].equals(loginUser.getNickname()) ? chatRoom.getRequestDealUserImageUrl() : chatRoom.getPostOwnerImageUrl())
                                                                 .messageList(messageDtoList)
                                                                 .build());
    }

    @Transactional
    public ResponseDto<?> deleteChatroomInRedisAndMySQL(Long userId, String roomId, UserDetailsImpl userDetails) {

        User loginUser = userDetails.getUser();

        if(loginUser == null || !loginUser.getId().equals(userId)) { return ResponseDto.fail(ErrorCode.USER_NOT_FOUND); }

        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(()-> new IllegalArgumentException("해당 채팅방을 찾을 수 없습니다."));

        if(!chatRoom.getPostOwnerNickname().equals(loginUser.getNickname()) && !chatRoom.getRequestDealUserNickname().equals(loginUser.getNickname())){
            return ResponseDto.fail(ErrorCode.INVALID_CHATROOM_OWNER_OR_DEALER);
        }

        chatRoom.updatePostStatusToDeleted();

        return ResponseDto.success(MessageResponseDto.builder().msg("Redis - 채팅방 캐시 데이터 삭제 완료. (ChatRoom Id : " + roomId + ")").build());
    }

    // MySQL 채팅방 삭제 : Scheduler를 활용해 MySQL에서 채팅방의 Status가 Deleted로 변경된 지 7일이 지났을 경우 매일 오전 2시에 일괄 삭제
    @Transactional
    public void deleteChatRoomsInMySQL(String roomId) {

        chatRoomRepository.deleteByRoomId(roomId);
        log.info("[Scheduler] MySQL - 채팅방 데이터 삭제 완료. (ChatRoom Id : " + roomId + ")");
    }
}
