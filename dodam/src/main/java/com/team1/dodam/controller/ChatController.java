package com.team1.dodam.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team1.dodam.domain.ChatMessage;
import com.team1.dodam.domain.ChatRoom;
import com.team1.dodam.domain.User;
import com.team1.dodam.domain.UserDetailsImpl;
import com.team1.dodam.dto.request.MessageRequestDto;
import com.team1.dodam.jwt.TokenProvider;
import com.team1.dodam.repository.ChatMessageRepository;
import com.team1.dodam.repository.ChatRoomRepository;
import com.team1.dodam.repository.UserRepository;
import io.lettuce.core.output.ScanOutput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;

@Slf4j
@RequiredArgsConstructor
@Controller
public class ChatController {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    // Redis
    private static final String CHAT_MESSAGE = "CHAT_MESSAGE";
    private ListOperations<String, Object> opsListChatMessage;

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic channelTopic;
    private final SimpMessageSendingOperations messagingTemplate;
    @PostConstruct
    private void init() {
        opsListChatMessage = redisTemplate.opsForList();
    }

    /**
     * websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
     */
    @MessageMapping("/chat/message/{roomId}")
    public void message(MessageRequestDto message, @Header(value = "Authorization") String token,
                        @DestinationVariable String roomId) throws JsonProcessingException {
        String[] check = token.split("\\.");
        Base64.Decoder decoder = Base64.getDecoder();
        String payload = new String(decoder.decode(check[1]));

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> returnMap = mapper.readValue(payload, Map.class);
        String email = returnMap.get("sub").toString();

        User user = userRepository.findByEmail(email).orElseThrow(
                ()-> new IllegalArgumentException("회원정보를 찾을 수 없습니다."));
        // 로그인 회원 정보로 대화명 설정
        message.setSender(user.getNickname());
        // Websocket에 발행된 메시지를 redis로 발행(publish)
        redisTemplate.convertAndSend(channelTopic.getTopic(), message);

        chatMessageRepository.save(ChatMessage.builder()
                                              .chatRoom(chatRoomRepository.findById(message.getRoomId())
                                                        .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다.")))
                                              .user(user)
                                              .type(message.getType())
                                              .message(message.getMessage())
                                              .build());
    }

    @GetMapping("/messages/{roomId}")
    public List<ChatMessage> getMessages(@PathVariable String roomId){

        ChatRoom chatroom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));
        List<ChatMessage> messages =chatMessageRepository.findAllByChatRoom(chatroom);
//        Long size = opsListChatMessage.size(roomId);
//        System.out.println(opsListChatMessage.range(roomId, 0 , size));
//        return opsListChatMessage.range(roomId, 0 , size);
        return chatMessageRepository.findAllByChatRoom(chatroom);
    }
}
