package com.team1.dodam.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team1.dodam.domain.ChatMessage;
import com.team1.dodam.domain.User;
import com.team1.dodam.dto.request.MessageRequestDto;
import com.team1.dodam.dto.response.ChatResponseDto;
import com.team1.dodam.dto.response.MessageResponseDto;
import com.team1.dodam.dto.response.ResponseDto;
import com.team1.dodam.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisSubscriber  {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate;
//    private final RedisTemplate redisTemplate;

    /**
     * Redis에서 메시지가 발행(publish)되면 대기하고 있던 Redis Subscriber가 해당 메시지를 받아 처리한다.
     */
    public void sendMessage(String publishMessage) {
        try {
            // MessageRequestDto 객채로 맵핑
            MessageRequestDto requestMessage = objectMapper.readValue(publishMessage, MessageRequestDto.class);
            User user = userRepository.findByNickname(requestMessage.getSender()).orElseThrow(
                    () -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다.")
            );
            ChatResponseDto chatMessage = ChatResponseDto.builder()
                                                                .message(requestMessage.getMessage())
                                                                .roomId(requestMessage.getRoomId())
                                                                .sender(requestMessage.getSender())
                                                                .type(requestMessage.getType())
                                                                .senderId(user.getId())
                                                                .build();
            // 채팅방을 구독한 클라이언트에게 메시지 발송
            messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getRoomId(), chatMessage);
        } catch (Exception e) {
            log.error("Exception {}", e);
        }
    }

//    @Override
//    public void onMessage(Message message, byte[] pattern) {
//        try {
//            // redis에서 발행된 데이터를 받아 deserialize
//            String publishMessage = (String) redisTemplate.getStringSerializer().deserialize(message.getBody());
//            // ChatMessage 객채로 맵핑
//            ChatMessage roomMessage = objectMapper.readValue(publishMessage, ChatMessage.class);
//            // Websocket 구독자에게 채팅 메시지 Send
//            messagingTemplate.convertAndSend("/sub/chat/room/" + roomMessage.getChatRoom().getRoomId(), roomMessage);
//        } catch (Exception e) {
//            log.error(e.getMessage());
//        }
//    }
}
