package com.iuh.edu.fit.BEJewelry.Architecture.service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.iuh.edu.fit.BEJewelry.Architecture.domain.User;
import com.iuh.edu.fit.BEJewelry.Architecture.repository.UserRepository;
import com.iuh.edu.fit.BEJewelry.Architecture.util.error.PermissionException;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService; // Service gửi email

    public String forgotPassword(String email) {
        User user = userRepository.findByEmail(email);
        // if (userOpt.isEmpty()) {
        // throw new PermissionException("Không tìm thấy tài khoản với email này.");
        // }

        // User user = userOpt.get();
        String token = generateResetToken();
        user.setResetToken(token);
        userRepository.save(user);

        String resetLink = "http://localhost:4173/reset-password?token=" + token;
        try {
            emailService.sendResetPasswordEmail(user.getEmail(), resetLink);
        } catch (MessagingException e) {
            throw new RuntimeException("Lỗi khi gửi email: " + e.getMessage());
        }

        return "Email đặt lại mật khẩu đã được gửi.";
    }

    @Transactional
    public String resetPassword(String token, String newPassword) throws PermissionException {
        Optional<User> userOpt = userRepository.findByResetToken(token);
        if (userOpt.isEmpty()) {
            throw new PermissionException("Token không hợp lệ hoặc đã hết hạn.");
        }

        User user = userOpt.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        userRepository.save(user);

        return "Mật khẩu đã được đặt lại thành công.";
    }

    private String generateResetToken() {
        byte[] randomBytes = new byte[32];
        new SecureRandom().nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}
