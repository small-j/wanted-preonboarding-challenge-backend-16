package com.wanted.preonboarding.ticket.application;

import com.wanted.preonboarding.ticket.domain.dto.MailContent;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailSender {
    private final JavaMailSender javaMailSender;

    public boolean sendEmail(MailContent mailContent) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mailContent.getEmail());
//        message.setFrom("wanted@preonboarding-16.com"); // 이 메서드를 호출해도 application.yml의 username으로 설정된다.
        message.setSubject(mailContent.getTitle());
        message.setText(mailContent.getMessage());

        javaMailSender.send(message);
        return true;
    }
}
