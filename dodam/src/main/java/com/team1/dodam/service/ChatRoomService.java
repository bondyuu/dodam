package com.team1.dodam.service;

import com.team1.dodam.domain.*;
import com.team1.dodam.dto.ChatMessageDto;
import com.team1.dodam.dto.response.*;
import com.team1.dodam.redis.RedisSubscriber;
import com.team1.dodam.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ChatRoomService {

    private final NotificationRepository notificationRepository;
    private final PostRepository postRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;


    private final NotificationService notificationService;
//    private final RedisTemplate<String, Object> redisTemplate;
//    private HashOperations<String, String, ChatRoom> opsHashChatRoom;
//    private Map<String, ChannelTopic> topics;
//    private HashOperations<String, String, String> hashOpsEnterInfo;
//
//    @PostConstruct
//    private void init() {
//        opsHashChatRoom = redisTemplate.opsForHash();
//        hashOpsEnterInfo=redisTemplate.opsForHash();
//
//        topics = new HashMap<>();
//    }




    // 모든 채팅방 조회
    @Transactional
    public List<ChatRoomListResponseDto> findAllRoom(UserDetailsImpl userDetails) {
//        return opsHashChatRoom.values(CHAT_ROOMS);
        User loginUser = userDetails.getUser();
        List<ChatRoom> chatRoomList1 = chatRoomRepository.findAllByUser1(loginUser);
        List<ChatRoom> chatRoomList2 = chatRoomRepository.findAllByuser2(loginUser);
        List<ChatRoomListResponseDto> chatRoomList = chatRoomList1.stream()
                                                                  .map(chatRoom -> ChatRoomListResponseDto.builder()
                                                                                                          .roomId(chatRoom.getRoomId())
                                                                                                          .profileUrl(chatRoom.getUser2().getProfileUrl())
                                                                                                          .location(chatRoom.getUser2().getLocation())
                                                                                                          .nickname(chatRoom.getUser2().getNickname())
                                                                                                          .lastMessage(chatMessageRepository.findTopByChatRoom(chatRoom,Sort.by("createdAt")) ==null ? "" : chatMessageRepository.findTopByChatRoom(chatRoom,Sort.by("createdAt")).getMessage())
                                                                                                          .lastTime(chatMessageRepository.findTopByChatRoom(chatRoom,Sort.by("createdAt")) ==null ? "" :MyPageService.formatTime(chatMessageRepository.findTopByChatRoom(chatRoom,Sort.by("createdAt")).getCreatedAt()))
                                                                                                          .build())
                                                                  .collect(Collectors.toList());
        chatRoomList.addAll(chatRoomList2.stream()
                                         .map(chatRoom -> ChatRoomListResponseDto.builder()
                                                                                 .roomId(chatRoom.getRoomId())
                                                                                 .profileUrl(chatRoom.getUser1().getProfileUrl())
                                                                                 .location(chatRoom.getUser1().getLocation())
                                                                                 .nickname(chatRoom.getUser1().getNickname())
                                                                                 .lastMessage(chatMessageRepository.findTopByChatRoom(chatRoom,Sort.by("createdAt")) ==null ? "" : chatMessageRepository.findTopByChatRoom(chatRoom,Sort.by("createdAt")).getMessage())
                                                                                 .lastTime(chatMessageRepository.findTopByChatRoom(chatRoom,Sort.by("createdAt")) ==null ? "" :MyPageService.formatTime(chatMessageRepository.findTopByChatRoom(chatRoom,Sort.by("createdAt")).getCreatedAt()))
                                                                                 .build())
                                         .collect(Collectors.toList()));
        return chatRoomList;
    }

    // 특정 채팅방 조회
    @Transactional
    public ChatRoomDetailResponseDto findRoomById(String roomId, UserDetailsImpl userDetails) {
//        return opsHashChatRoom.get(CHAT_ROOMS, id);
        User loginUser = userDetails.getUser();
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(
                ()-> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        User user = (chatRoom.getUser1()==loginUser)?chatRoom.getUser2():chatRoom.getUser1();
        List<ChatMessage> messageList = chatMessageRepository.findAllByChatRoom(chatRoom);
        List<ChatMessageDto> messageDtoList = messageList.stream()
                                                         .map(message -> ChatMessageDto.builder()
                                                                                       .type(message.getType())
                                                                                       .sender(message.getUser().getNickname())
                                                                                       .message(message.getMessage())
                                                                                       .createdAt(message.getCreatedAt())
                                                                                       .senderId(message.getUser().getId())
                                                                                       .build())
                                                         .collect(Collectors.toList());
        return ChatRoomDetailResponseDto.builder()
                                        .roomId(chatRoom.getRoomId())
                                        .postId(chatRoom.getPost().getId())
                                        .postImage(chatRoom.getPost().getImageList().get(0).getImageUrl())
                                        .postTitle(chatRoom.getPost().getTitle())
                                        .userId(user.getId())
                                        .nickname(user.getNickname())
                                        .profileUrl(user.getProfileUrl())
//                                        .messageList(chatMessageRepository.findAllByChatRoom(chatRoom))
                                        .messageList(messageDtoList)
                                        .build();
    }

    // 채팅방 생성 : 서버간 채팅방 공유를 위해 redis hash에 저장한다.
    @Transactional
    public ResponseDto<?> createChatRoom(UserDetailsImpl userDetails, Long postId) {
        User loginUser = userDetails.getUser();
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        ChatRoom chatRoom = chatRoomRepository.findByPostAndUser2(post, loginUser).orElse(null);


//        // 시도1~
//        String chatRoomName = post.getUser().getNickname() + ":" + loginUser.getNickname();
//        // ~시도1
//
//        // 시도3~
//        ChatRoom newRoom = chatRoomRepository.save(ChatRoom.create(post.getUser(), loginUser, post));
//        opsHashChatRoom.put(chatRoomName, newRoom.getRoomId(), newRoom);
//        return ResponseDto.success(ChatRoomResponseDto.builder().msg("INIT").roomId(newRoom.getRoomId()).build());
//        // ~시도3

        if(chatRoom == null) {
            ChatRoom newRoom = chatRoomRepository.save(ChatRoom.create(post.getUser(), loginUser, post));
            notificationRepository.save(new Notification(newRoom.getUser1(),newRoom, newRoom.getUser2().getNickname()+"님이 채팅을 시작했습니다.",false));
//            // 시도2~
//            opsHashChatRoom.put(chatRoomName, newRoom.getRoomId(), newRoom);
//            // ~시도2

            return ResponseDto.success(ChatRoomResponseDto.builder().msg("INIT").roomId(newRoom.getRoomId()).build());
        } else{
            return ResponseDto.success(ChatRoomResponseDto.builder().msg("EXIST").roomId(chatRoom.getRoomId()).build());
        }
//        opsHashChatRoom.put(CHAT_ROOMS, chatRoom.getRoomId(), chatRoom);
    }
}