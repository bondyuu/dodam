package com.team1.dodam.service;

import com.team1.dodam.domain.CertificationNumber;
import com.team1.dodam.dto.MailDto;
import com.team1.dodam.dto.request.MailRequestDto;
import com.team1.dodam.dto.response.ResponseDto;
import com.team1.dodam.repository.CertificationNumberRepository;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Random;

@Service
@AllArgsConstructor
public class EmailService {

    private final CertificationNumberRepository certificationNumberRepository;
    private JavaMailSender emailSender;

    private final TemplateEngine templateEngine;

    @Transactional
    public ResponseDto<?> send(MailRequestDto requestDto) throws MessagingException, IOException {

        int certificationNumber = makeRandomNumber();
        String title = "회원가입 인증 메일입니다.";
        String emailFrom = "team3mailsender@gmail.com";

        certificationNumberRepository.deleteByEmail(requestDto.getAddress());


        CertificationNumber certification = certificationNumberRepository.save(CertificationNumber.builder()
                                                              .email(requestDto.getAddress())
                                                              .certificationNumber((long)certificationNumber)
                                                              .build());

//        SimpleMailMessage message = new SimpleMailMessage();
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(emailFrom);
        helper.setTo(certification.getEmail());
        helper.setSubject(title);
        //템플릿에 전달할 데이터 설정
        Context context = new Context();
        context.setVariable("num", Long.toString(certification.getCertificationNumber()));

        //메일 내용 설정 : 템플릿 프로세스
        String html = templateEngine.process("mail", context);
        helper.setText(html, true);

        emailSender.send(message);
        return ResponseDto.success("success");
    }

    public int makeRandomNumber() {
        Random r = new Random();
        return r.nextInt(899999)+100000;
    }
}  
