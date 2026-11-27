package com.bridgelabz.fundoonotes.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    private static final Logger log = LoggerFactory.getLogger(MailService.class);
    private final ObjectProvider<JavaMailSender> sender;
    private final String from;
    private final boolean logOnly;

    public MailService(ObjectProvider<JavaMailSender> sender,
                       @Value("${fundoo.mail.from:no-reply@localhost}") String from,
                       @Value("${fundoo.mail.log-only:false}") boolean logOnly) {
        this.sender = sender;
        this.from = from;
        this.logOnly = logOnly;
    }

    public void send(String to, String subject, String body) {
        JavaMailSender mailSender = sender.getIfAvailable();
        if (mailSender == null) {
            if (logOnly) {
                log.info("Development email to {}: {} | {}", to, subject, body);
                return;
            }
            throw new MailSendException("SMTP is not configured");
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}
