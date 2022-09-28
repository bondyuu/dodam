package com.team1.dodam.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team1.dodam.domain.*;
import com.team1.dodam.dto.ChatMessageDto;
import com.team1.dodam.dto.response.*;
import com.team1.dodam.global.error.ErrorCode;
import com.team1.dodam.repository.ChatMessageRepository;
import com.team1.dodam.repository.ChatRoomRepository;
import com.team1.dodam.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatRoomService {

    private final PostRepository postRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
//    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;
//    private ValueOperations<String, Object> opsValueChatRoom;
    private HashOperations<String, String, ChatRoom> opsHashChatRoom;
//    private Map<String, ChannelTopic> topics;
//    private HashOperations<String, String, String> hashOpsEnterInfo;

    private final ObjectMapper objectMapper;

    @PostConstruct
    private void init() {
//        opsValueChatRoom = redisTemplate.opsForValue();
        opsHashChatRoom = redisTemplate.opsForHash();
    }




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
                                                                                                          .dealerImageUrl(chatRoom.getUser2().getProfileUrl())
                                                                                                          .dealerLocation(chatRoom.getUser2().getLocation())
                                                                                                          .dealerNickname(chatRoom.getUser2().getNickname())
                                                                                                          .lastMessage(chatMessageRepository.findTopByChatRoom(chatRoom,Sort.by("createdAt")) ==null ? "" : chatMessageRepository.findTopByChatRoom(chatRoom,Sort.by("createdAt")).getMessage())
                                                                                                          .modifiedAt(chatMessageRepository.findTopByChatRoom(chatRoom,Sort.by("createdAt")) ==null ? "" :MyPageService.formatTime(chatMessageRepository.findTopByChatRoom(chatRoom,Sort.by("createdAt")).getCreatedAt()))
                                                                                                          .build())
                                                                  .collect(Collectors.toList());
        chatRoomList.addAll(chatRoomList2.stream()
                                         .map(chatRoom -> ChatRoomListResponseDto.builder()
                                                                                 .roomId(chatRoom.getRoomId())
                                                                                 .dealerImageUrl(chatRoom.getUser1().getProfileUrl())
                                                                                 .dealerLocation(chatRoom.getUser1().getLocation())
                                                                                 .dealerNickname(chatRoom.getUser1().getNickname())
                                                                                 .lastMessage(chatMessageRepository.findTopByChatRoom(chatRoom,Sort.by("createdAt")) ==null ? "" : chatMessageRepository.findTopByChatRoom(chatRoom,Sort.by("createdAt")).getMessage())
                                                                                 .modifiedAt(chatMessageRepository.findTopByChatRoom(chatRoom,Sort.by("createdAt")) ==null ? "" :MyPageService.formatTime(chatMessageRepository.findTopByChatRoom(chatRoom,Sort.by("createdAt")).getCreatedAt()))
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

    // 채팅방 생성 : 채팅방 정보를 Redis와 MySQL에 모두 저장
    @Transactional
    public ResponseDto<?> createChatRoom(UserDetailsImpl userDetails, Long postId) {
        User loginUser = userDetails.getUser();
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if(loginUser == null) { return ResponseDto.fail(ErrorCode.USER_NOT_FOUND); }
        if(Objects.equals(loginUser.getNickname(), post.getUser().getNickname())) { return ResponseDto.fail(ErrorCode.DUPLICATED_DEALER_NICKNAME); }

        ChatRoom chatRoom = chatRoomRepository.findByPostAndUser2(post, loginUser).orElse(null);


        // 시도1~
//        String chatRoomName = post.getUser().getNickname() + ":" + loginUser.getNickname();
        String chatRoomName = "ListOfChatroom";
        // ~시도1

        // 시도3~
//        ChatRoom newRoom = chatRoomRepository.save(ChatRoom.create(post.getUser(), loginUser, post));
//        opsHashChatRoom.put(chatRoomName, newRoom.getRoomId(), newRoom);
//        return ResponseDto.success(ChatRoomResponseDto.builder().msg("INIT").roomId(newRoom.getRoomId()).build());
//        // ~시도3

        if(chatRoom == null) {
            ChatRoom newRoom = chatRoomRepository.save(ChatRoom.create(post.getUser(), loginUser, post));

            // 시도2~
            opsHashChatRoom.put(chatRoomName, newRoom.getRoomName(), newRoom);
//            opsValueChatRoom.set(newRoom.getRoomName(), newRoom);
            // ~시도2

            return ResponseDto.success(ChatRoomResponseDto.builder().msg("INIT").roomId(newRoom.getRoomId()).build());
        } else{
            return ResponseDto.success(ChatRoomResponseDto.builder().msg("EXIST").roomId(chatRoom.getRoomId()).build());
        }
//        opsHashChatRoom.put(CHAT_ROOMS, chatRoom.getRoomId(), chatRoom);
    }

    // Redis 채팅방 전체 조회
    public ResponseDto<?> findChatroomAllInRedis(UserDetailsImpl userDetails) {

        User loginUser = userDetails.getUser();

        if(loginUser == null) { return ResponseDto.fail(ErrorCode.USER_NOT_FOUND); }

        List<ChatRoom> redisList = objectMapper.convertValue(opsHashChatRoom.values("ListOfChatroom"), new TypeReference<>(){});

        return ResponseDto.success(redisList.stream().filter(c -> c.getRoomName().split(":")[0].equals(loginUser.getNickname()) || c.getRoomName().split(":")[1].equals(loginUser.getNickname()))
                                                     .map(chatRoom -> ChatRoomListResponseDto.builder()
                                                                                             .roomId(chatRoom.getRoomId())
                                                                                             .roomName(chatRoom.getRoomName())
                                                                                             .dealerImageUrl(chatRoom.getRoomName().split(":")[0].equals(loginUser.getNickname()) ? chatRoom.getRequestDealUserImageUrl() : chatRoom.getPostOwnerImageUrl())
                                                                                             .dealerNickname(chatRoom.getRoomName().split(":")[0].equals(loginUser.getNickname()) ? chatRoom.getRequestDealUserNickname() : chatRoom.getPostOwnerNickname())
                                                                                             .dealerLocation(chatRoom.getRoomName().split(":")[0].equals(loginUser.getNickname()) ? chatRoom.getRequestDealUserLocation() : chatRoom.getPostOwnerLocation())
                                                                                             .itemImageUrl(chatRoom.getItemImageUrl())
                                                                                             .lastMessage(Objects.equals(chatRoom.getMessageList(), null) ? "" : String.valueOf(chatRoom.getMessageList().get(chatRoom.getMessageList().size() - 1)))
                                                                                             .modifiedAt(MyPageService.formatTime(chatRoom.getModifiedAt()))
                                                                                             .build())
                                                     .collect(Collectors.toList()));
    }

    // Redis 채팅방 상세 조회
    @Transactional
    public ResponseDto<?> findDetailChatroomInRedis(String roomId, UserDetailsImpl userDetails) {

        User loginUser = userDetails.getUser();

        if(loginUser == null) { return ResponseDto.fail(ErrorCode.USER_NOT_FOUND); }

        // Redis
        List<ChatRoom> redisList = objectMapper.convertValue(opsHashChatRoom.values("ListOfChatroom"), new TypeReference<>(){});

        // MySQL
        ChatRoom mySQLChatRoom = chatRoomRepository.findById(roomId).orElseThrow(()-> new IllegalArgumentException("해당 채팅방을 찾을 수 없습니다."));

        User user = (loginUser.equals(mySQLChatRoom.getUser1())) ? mySQLChatRoom.getUser2() : mySQLChatRoom.getUser1();

        List<ChatMessage> mySQLMessageList = chatMessageRepository.findAllByChatRoom(mySQLChatRoom);

        List<ChatMessageDto> mySQLMessageDtoList = mySQLMessageList.stream()
                                                                   .map(message -> ChatMessageDto.builder()
                                                                                                 .type(message.getType())
                                                                                                 .sender(message.getUser().getNickname())
                                                                                                 .message(message.getMessage())
                                                                                                 .createdAt(message.getCreatedAt())
                                                                                                 .build())
                                                                   .collect(Collectors.toList());

        ChatRoomDetailResponseDto chatRoomDetailResponseDto = ChatRoomDetailResponseDto.builder()
                                                                                       .roomId(mySQLChatRoom.getRoomId())
                                                                                       .postId(mySQLChatRoom.getPost().getId())
                                                                                       .postImage(mySQLChatRoom.getPost().getImageList().get(0).getImageUrl())
                                                                                       .postTitle(mySQLChatRoom.getPost().getTitle())
                                                                                       .userId(user.getId())
                                                                                       .nickname(user.getNickname())
                                                                                       .profileUrl(user.getProfileUrl())
                                                                                       .messageList(mySQLMessageDtoList)
                                                                                       .build();

        return ResponseDto.success(Objects.equals(redisList, null) ? chatRoomDetailResponseDto : redisList.stream().filter(c -> c.getRoomId().equals(roomId))
                                                                                                                      .map(chatRoom -> RedisChatRoomDetailResponseDto.builder()
                                                                                                                                                              .roomName(chatRoom.getRoomName())
                                                                                                                                                              .itemName(chatRoom.getItemName())
                                                                                                                                                              .itemImageUrl(chatRoom.getItemImageUrl())
                                                                                                                                                              .dealerNickname(chatRoom.getRoomName().split(":")[0].equals(loginUser.getNickname()) ? chatRoom.getRequestDealUserNickname() : chatRoom.getPostOwnerNickname())
                                                                                                                                                              .dealerImageUrl(chatRoom.getRoomName().split(":")[0].equals(loginUser.getNickname()) ? chatRoom.getRequestDealUserImageUrl() : chatRoom.getPostOwnerImageUrl())
                                                                                                                                                              .messageList(Objects.equals(chatRoom.getMessageList(), null) ? mySQLMessageDtoList : null)
                                                                                                                                                              .build())
                                                                                                                      .collect(Collectors.toList()));
    }

    // Redis 채팅방 삭제
    @Transactional
    public ResponseDto<?> deleteChatroomInRedis(String roomId, UserDetailsImpl userDetails) {

        User loginUser = userDetails.getUser();

        if(loginUser == null) { return ResponseDto.fail(ErrorCode.USER_NOT_FOUND); }

        // Redis
        String hashKey = "";
        List<ChatRoom> redisList = objectMapper.convertValue(opsHashChatRoom.values("ListOfChatroom"), new TypeReference<>(){});
        List<String> hashKeys = objectMapper.convertValue(opsHashChatRoom.keys("ListOfChatroom"), new TypeReference<>() {});


        for(String hK : hashKeys) {
            if(hK.split(":")[2].equals(roomId)) {
                hashKey = hK;
                break;
            }
        }

        if(!hashKey.split(":")[0].equals(loginUser.getNickname()) || !hashKey.split(":")[1].equals(loginUser.getNickname())) {
            return ResponseDto.fail(ErrorCode.INVALID_CHATROOM_OWNER_OR_DEALER);
        }

        // MySQL
        ChatRoom mySQLChatRoom = chatRoomRepository.findById(roomId).orElseThrow(()-> new IllegalArgumentException("해당 채팅방을 찾을 수 없습니다."));

        mySQLChatRoom.updatePostStatusToDeleted();
        opsHashChatRoom.delete("ListOfChatroom", hashKey);

        return ResponseDto.success(MessageResponseDto.builder().msg("채팅방을 삭제했습니다.").build());
    }

    // MySQL 채팅방 삭제 : Scheduler를 활용해 MySQL에서 채팅방의 Status가 Deleted로 변경된 지 7일이 지났을 경우 매일 오전 2시에 일괄 삭제
    @Transactional
    public void deleteChatRoomsInMySQL(String roomId) {

        chatRoomRepository.deleteByRoomId(roomId);
        log.info("[Scheduler] 채팅방 데이터 삭제 완료. (ChatRoom Id : " + roomId + ")");
    }
}
