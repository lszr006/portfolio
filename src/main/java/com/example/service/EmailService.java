package com.example.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${contact.email.to}")
    private String toAddress;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendContactEmail(String name, String fromEmail, String messageBody) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toAddress);
        message.setSubject("Portfolio Contact: " + name);
        message.setText("From: " + name + " (" + fromEmail + ")\n\n" + messageBody);
        message.setReplyTo(fromEmail);
        mailSender.send(message);
    }
}
