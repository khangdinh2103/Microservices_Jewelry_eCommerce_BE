package com.iuh.edu.fit.BEJewelry.Architecture.service;

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

    public void sendResetPasswordEmail(String to, String resetLink) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject("Đặt lại mật khẩu của bạn");
        helper.setText("<p>Nhấp vào link bên dưới để đặt lại mật khẩu của bạn:</p>"
                + "<p><a href=\"" + resetLink + "\">Đặt lại mật khẩu</a></p>", true);

        mailSender.send(message);
    }
}
