package com.team1.dodam.service;

import com.team1.dodam.domain.ChatRoom;
import com.team1.dodam.domain.Notification;
import com.team1.dodam.domain.User;
import com.team1.dodam.dto.response.NotificationResponseDto;
import com.team1.dodam.repository.EmitterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    private final EmitterRepository emitterRepository;

    public SseEmitter subscribe(Long userId, String lastEventId) {
        // 1
        String id = userId + "_" + System.currentTimeMillis();

        // 2 (userId, id, emitter)형식으로 저장
        SseEmitter emitter = emitterRepository.save(userId, id, new SseEmitter(DEFAULT_TIMEOUT));

        emitter.onCompletion(() -> emitterRepository.deleteById(userId, id));
        emitter.onTimeout(() -> emitterRepository.deleteById(userId, id));

        // 3
        // 503 에러를 방지하기 위한 더미 이벤트 전송
        sendToClient(emitter, id, "EventStream Created. [userId=" + userId + "]");

//        // 4
//        // 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실을 예방
//        if (!lastEventId.isEmpty()) {
//            Map<String, SseEmitter> events = emitterRepository.findAllEventCacheStartWithId(String.valueOf(userId));
//            events.entrySet().stream()
//                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
//                    .forEach(entry -> sendToClient(emitter, entry.getKey(), entry.getValue()));
//        }

        return emitter;
    }

    public void send(User user, ChatRoom chatRoom, String content) {
        Notification notification = createNotification(user, chatRoom, content);
        String id = String.valueOf(user.getId());

        // 로그인 한 유저의 SseEmitter 모두 가져오기
        Map<String, SseEmitter> sseEmitters = emitterRepository.findAllStartWithId(id);
        sseEmitters.forEach(
                (key, emitter) -> {
//                    // 데이터 캐시 저장(유실된 데이터 처리하기 위함)
//                    emitterRepository.saveEventCache(key, notification);
                    // 데이터 전송
                    sendToClient(emitter, key, NotificationResponseDto.from(notification));
                }
        );
    }

    private Notification createNotification(User user, ChatRoom chatRoom, String content) {
        return Notification.builder()
                .user(user)
                .content(content)
                .chatRoom(chatRoom)
                .isRead(false)
                .build();
    }

    private void sendToClient(SseEmitter emitter, String id, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(id)
                    .name("sse")
                    .data(data));
        } catch (IOException exception) {
            Long userId = Long.parseLong(id.split("_")[0]);
            emitterRepository.deleteById(userId, id);
            throw new RuntimeException("연결 오류!");
        }
    }
}