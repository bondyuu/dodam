package com.team1.dodam.controller;

import com.team1.dodam.dto.request.MailRequestDto;
import com.team1.dodam.dto.response.ResponseDto;
import com.team1.dodam.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class MailController {

    private final EmailService emailService;

    @PostMapping("/mail/send")
    public ResponseDto<?> sendMail(@RequestBody MailRequestDto requestDto) throws MessagingException, IOException {
        return emailService.send(requestDto);
    }
}
