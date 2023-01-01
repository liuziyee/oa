package com.dorohedoro.job;

import com.dorohedoro.config.Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MailJob {

    private final JavaMailSender mailSender;
    private final Properties properties;
    
    @Async("pool")
    public void send(SimpleMailMessage msg) {
        String mailBox = properties.getMail().getSystem();
        msg.setFrom(mailBox);
        msg.setCc(mailBox);
        mailSender.send(msg);
    }
}
