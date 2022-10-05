package com.team1.dodam.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team1.dodam.domain.ChatRoom;
import com.team1.dodam.domain.Notification;
import com.team1.dodam.domain.User;
import com.team1.dodam.domain.UserDetailsImpl;
import com.team1.dodam.dto.response.MessageResponseDto;
import com.team1.dodam.dto.response.NotificationResponseDto;
import com.team1.dodam.dto.response.ResponseDto;
import com.team1.dodam.repository.NotificationRepository;
import com.team1.dodam.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private static final Map<Long, SseEmitter> sseEmitters = new ConcurrentHashMap<>();

    @Transactional
    public SseEmitter subscribe(Long userId) {

        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        sendInit(emitter,String.valueOf(userId),"");
        sseEmitters.put(userId, emitter);

        emitter.onCompletion(() -> sseEmitters.remove(userId));
        emitter.onTimeout(() -> sseEmitters.remove(userId));

//        sendToClient(emitter,String.valueOf(userId),"");
//        User user = userRepository.findById(userId).orElseThrow(
//                ()-> new IllegalArgumentException("회원 정보를 찾을 수 없습니다.")
//        );
//        List<Notification> notificationList = notificationRepository.findAllByUser(user)
//                .stream()
//                .filter(c -> c.getIsRead().equals(false))
//                .collect(Collectors.toList());
//        if (notificationList.size() != 0){
//            notificationList.forEach(notification -> sendToClient(emitter, String.valueOf(userId), "알림"));
//        }

        return emitter;
    }
    public void saveAndSend(ChatRoom chatRoom) {
        Long userId = chatRoom.getUser1().getId();
        if(sseEmitters.containsKey(userId)) {
            sendToClient(sseEmitters.get(userId),String.valueOf(userId),"알림");
        }
        notificationRepository.save(new Notification(chatRoom.getUser1(),chatRoom, "알림",false));

    }

    private void sendToClient(SseEmitter emitter, String id, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(id)
                    .name("message")
                    .data(data+"/n/n"));
        } catch (IOException exception) {
            Long userId = Long.parseLong(id);
            sseEmitters.remove(userId);
            throw new RuntimeException("연결 오류!");
        }
    }
    private void sendInit(SseEmitter emitter, String id, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(id)
                    .name("init")
                    .data(data+"/n/n"));
        } catch (IOException exception) {
            Long userId = Long.parseLong(id);
            sseEmitters.remove(userId);
            throw new RuntimeException("연결 오류!");
        }
    }

    @Transactional
    public ResponseDto<?> changeIsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(
                ()-> new IllegalArgumentException("알림을 찾을 수 없습니다.")
        );
        notification.changeIsRead();
        return ResponseDto.success(String.valueOf(notification.getIsRead()));
    }

    @Transactional
    public ResponseDto<?> getNotifications(UserDetailsImpl userDetails) {
        User user = userDetails.getUser();

        List<Notification> notificationList = notificationRepository.findAllByUser(user).stream()
                .filter(c -> c.getIsRead().equals(false))
                .collect(Collectors.toList());;

        return ResponseDto.success(notificationList.stream().map(NotificationResponseDto::from).collect(Collectors.toList()));
    }

    @Transactional
    public ResponseDto<?> readNotifications(UserDetailsImpl userDetails) {
        List<Notification> notificationList = notificationRepository.findAllByUser(userDetails.getUser());
        notificationList.forEach(Notification::changeIsRead);
        return ResponseDto.success(MessageResponseDto.builder().msg("읽음 처리").build());
    }
}