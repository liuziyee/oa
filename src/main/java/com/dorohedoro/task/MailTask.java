package com.dorohedoro.task;

import com.dorohedoro.config.Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MailTask {

    private final JavaMailSender mailSender;
    private final Properties properties;
    
    @Async
    public void run(SimpleMailMessage msg) {
        String mailBox = properties.getMail().getSystem();
        msg.setFrom(mailBox);
        msg.setCc(mailBox);
        mailSender.send(msg);
    }
}