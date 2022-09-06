package com.team1.dodam.controller;

import com.team1.dodam.dto.MailDto;
import com.team1.dodam.dto.response.ResponseDto;
import com.team1.dodam.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MailController {

    private final EmailService emailService;

    @PostMapping("/mail/send")
    public ResponseDto<?> sendMail(@RequestBody MailDto mailDto) {
        emailService.send(mailDto);
        return ResponseDto.success("Adsf");
    }
}
