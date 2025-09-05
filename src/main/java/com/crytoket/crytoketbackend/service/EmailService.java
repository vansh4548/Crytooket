package com.crytoket.crytoketbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your CRYTOKET Verification Code");
        message.setText("Your OTP for registration is: " + otp + "\nIt is valid for 5 minutes.");
        mailSender.send(message);
    }
    public void sendPasswordResetOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your CRYTOKET Password Reset Code");
        message.setText("Your OTP for password reset is: " + otp + "\nIt is valid for 5 minutes.");
        mailSender.send(message);
    }
}