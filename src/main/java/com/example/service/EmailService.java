package com.example.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${contact.email.to:}")
    private String toAddress;

    @Value("${spring.mail.username:}")
    private String fromAddress;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public boolean isConfigured() {
        return fromAddress != null && !fromAddress.isBlank()
            && toAddress != null && !toAddress.isBlank();
    }

    public void sendContactEmail(String name, String fromEmail, String messageBody) {
        if (!isConfigured()) {
            throw new IllegalStateException("Email is not configured");
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(toAddress);
        message.setSubject("Portfolio Contact: " + name);
        message.setText("From: " + name + " (" + fromEmail + ")\n\n" + messageBody);
        message.setReplyTo(fromEmail);
        mailSender.send(message);
    }
}
