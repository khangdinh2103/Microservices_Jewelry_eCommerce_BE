package com.iuh.edu.fit.BEJewelry.Architecture.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendSimpleEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
    
    public void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true); // true indicates HTML content
        
        mailSender.send(message);
    }

    
    public void sendResetPasswordEmail(String to, String resetLink) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject("Đặt lại mật khẩu của bạn");
        helper.setText("<p>Nhấp vào link bên dưới để đặt lại mật khẩu của bạn:</p>"
                + "<p><a href=\"" + resetLink + "\">Đặt lại mật khẩu</a></p>", true);

        mailSender.send(message);
    }

    public void sendOccasionReminderEmail(String to, String subject, String htmlContent) throws MessagingException {
        sendHtmlEmail(to, subject, htmlContent);
    }
}
