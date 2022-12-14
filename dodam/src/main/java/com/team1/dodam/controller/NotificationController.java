package com.team1.dodam.controller;


import com.team1.dodam.domain.UserDetailsImpl;
import com.team1.dodam.dto.response.ResponseDto;
import com.team1.dodam.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * @title 로그인 한 유저 sse 연결
     */
    @GetMapping(value = "/subscribe/{userId}", produces = "text/event-stream")
    public SseEmitter subscribe(@PathVariable Long userId) {
        return notificationService.subscribe(userId);
    }

    @PutMapping("/notification/{notificationId}")
    public ResponseDto<?> changeIsRead(@PathVariable Long notificationId) {
        return notificationService.changeIsRead(notificationId);
    }

    @PutMapping("/notification/read")
    public ResponseDto<?> readNotifications(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return notificationService.readNotifications(userDetails);
    }

    @GetMapping("/notifications")
    public ResponseDto<?> getNotifications(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return notificationService.getNotifications(userDetails);
    }
}
