package com.iuh.edu.fit.BEJewelry.Architecture.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username:no-reply@jewelry.com}")
    private String fromEmail;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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
        
        helper.setFrom(fromEmail);
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

    public void sendOccasionReminderEmail(String to, String userName, String occasionName, 
                                         LocalDate occasionDate, String recipientName, 
                                         String relationship, String giftPreferences) throws MessagingException {
        String subject = "Nhắc nhở: " + occasionName + " sắp đến!";
        
        log.info("Preparing to send reminder email to: {}, for occasion: {}, date: {}", 
                to, occasionName, occasionDate);
        
        String htmlContent = buildOccasionReminderEmailTemplate(userName, occasionName, 
                                                              occasionDate, recipientName, 
                                                              relationship, giftPreferences);
        
        sendHtmlEmail(to, subject, htmlContent);
        log.info("Successfully sent email reminder to: {}", to);
    }
    
    private String buildOccasionReminderEmailTemplate(String userName, String occasionName, 
                                                    LocalDate occasionDate, String recipientName, 
                                                    String relationship, String giftPreferences) {
        StringBuilder html = new StringBuilder();
        
        html.append("<!DOCTYPE html>");
        html.append("<html>");
        html.append("<head>");
        html.append("<meta charset=\"UTF-8\">");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; }");
        html.append(".header { background-color: #4a90e2; color: white; padding: 20px; text-align: center; border-radius: 5px 5px 0 0; }");
        html.append(".content { padding: 20px; border: 1px solid #ddd; border-top: none; border-radius: 0 0 5px 5px; }");
        html.append(".footer { text-align: center; margin-top: 20px; font-size: 12px; color: #777; }");
        html.append(".highlight { font-weight: bold; color: #4a90e2; }");
        html.append(".gift-ideas { background-color: #f9f9f9; padding: 15px; border-radius: 5px; margin-top: 20px; }");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        
        // Header
        html.append("<div class=\"header\">");
        html.append("<h1>Nhắc nhở dịp đặc biệt</h1>");
        html.append("</div>");
        
        // Content
        html.append("<div class=\"content\">");
        html.append("<p>Xin chào ").append(userName).append(",</p>");
        
        html.append("<p>Chúng tôi muốn nhắc bạn rằng <span class=\"highlight\">").append(occasionName).append("</span>");
        
        if (recipientName != null && !recipientName.isEmpty()) {
            html.append(" của <span class=\"highlight\">").append(recipientName).append("</span>");
            if (relationship != null && !relationship.isEmpty()) {
                html.append(" (").append(relationship).append(")");
            }
        }
        
        html.append(" sẽ diễn ra vào ngày <span class=\"highlight\">").append(occasionDate.format(DATE_FORMATTER)).append("</span>.</p>");
        
        // Gift preferences if available
        if (giftPreferences != null && !giftPreferences.isEmpty()) {
            html.append("<div class=\"gift-ideas\">");
            html.append("<h3>Gợi ý quà tặng:</h3>");
            html.append("<p>").append(giftPreferences).append("</p>");
            html.append("</div>");
        }
        
        html.append("<p>Hãy chuẩn bị để làm cho ngày đặc biệt này thật ý nghĩa!</p>");
        
        html.append("<p>Trân trọng,<br>Đội ngũ Jewelry</p>");
        html.append("</div>");
        
        // Footer
        html.append("<div class=\"footer\">");
        html.append("<p>Email này được gửi tự động từ hệ thống nhắc nhở dịp đặc biệt của Jewelry.</p>");
        html.append("</div>");
        
        html.append("</body>");
        html.append("</html>");
        
        return html.toString();
    }
}
