package com.team1.dodam.service;


import com.team1.dodam.controller.request.MailRequestDto;
import com.team1.dodam.controller.response.ResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@AllArgsConstructor
public class EmailService {

    private JavaMailSender emailSender;
    private String title = "회원가입 인증 메일입니다.";

    public ResponseDto<?> send(MailRequestDto requestDto) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("team3mailsender@gmail.com");
        message.setTo(requestDto.getAddress());
        message.setSubject(title);
        message.setText(Integer.toString(makeRandomNumber()));
        emailSender.send(message);
        return ResponseDto.success("success");
    }

    public int makeRandomNumber() {
        Random r = new Random();
        return r.nextInt(999999);
    }
}
