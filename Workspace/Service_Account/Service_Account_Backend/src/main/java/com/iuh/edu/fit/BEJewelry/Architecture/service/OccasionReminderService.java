package com.iuh.edu.fit.BEJewelry.Architecture.service;

import com.iuh.edu.fit.BEJewelry.Architecture.domain.OccasionReminder;
import com.iuh.edu.fit.BEJewelry.Architecture.domain.User;
import com.iuh.edu.fit.BEJewelry.Architecture.domain.request.ReqOccasionReminderDTO;
import com.iuh.edu.fit.BEJewelry.Architecture.domain.response.ResOccasionReminderDTO;
import com.iuh.edu.fit.BEJewelry.Architecture.repository.OccasionReminderRepository;
import com.iuh.edu.fit.BEJewelry.Architecture.repository.UserRepository;
import com.iuh.edu.fit.BEJewelry.Architecture.util.error.IdInvalidException;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OccasionReminderService {

    private final OccasionReminderRepository occasionReminderRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public OccasionReminder createOccasionReminder(String userEmail, ReqOccasionReminderDTO dto) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        OccasionReminder reminder = new OccasionReminder();
        reminder.setUser(user);
        reminder.setOccasionName(dto.getOccasionName());
        reminder.setOccasionDate(dto.getOccasionDate());
        reminder.setReminderDaysBefore(dto.getReminderDaysBefore());
        reminder.setNotes(dto.getNotes());
        reminder.setYearlyRecurring(dto.isYearlyRecurring());
        reminder.setRecipientName(dto.getRecipientName());
        reminder.setRelationship(dto.getRelationship());
        reminder.setGiftPreferences(dto.getGiftPreferences());

        return occasionReminderRepository.save(reminder);
    }

    public OccasionReminder updateOccasionReminder(String userEmail, Long id, ReqOccasionReminderDTO dto) throws IdInvalidException {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        OccasionReminder reminder = occasionReminderRepository.findByUserAndId(user, id);
        if (reminder == null) {
            throw new IdInvalidException("Occasion reminder not found");
        }

        reminder.setOccasionName(dto.getOccasionName());
        reminder.setOccasionDate(dto.getOccasionDate());
        reminder.setReminderDaysBefore(dto.getReminderDaysBefore());
        reminder.setNotes(dto.getNotes());
        reminder.setYearlyRecurring(dto.isYearlyRecurring());
        reminder.setRecipientName(dto.getRecipientName());
        reminder.setRelationship(dto.getRelationship());
        reminder.setGiftPreferences(dto.getGiftPreferences());
        // Reset reminder sent status when updating
        reminder.setReminderSent(false);

        return occasionReminderRepository.save(reminder);
    }

    public void deleteOccasionReminder(String userEmail, Long id) throws IdInvalidException {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        OccasionReminder reminder = occasionReminderRepository.findByUserAndId(user, id);
        if (reminder == null) {
            throw new IdInvalidException("Occasion reminder not found");
        }

        occasionReminderRepository.delete(reminder);
    }

    public List<ResOccasionReminderDTO> getUserOccasionReminders(String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        List<OccasionReminder> reminders = occasionReminderRepository.findByUser(user);
        return reminders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Page<ResOccasionReminderDTO> getUserOccasionReminders(String userEmail, Pageable pageable) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Page<OccasionReminder> reminders = occasionReminderRepository.findByUser(user, pageable);
        return reminders.map(this::convertToDTO);
    }

    public List<ResOccasionReminderDTO> getUpcomingOccasions(String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Lấy các dịp lặp lại hàng năm
        List<OccasionReminder> yearlyOccasions = occasionReminderRepository.findByUserAndIsYearlyRecurringTrue(user);

        // Lấy các dịp không lặp lại trong 30 ngày tới
        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysLater = today.plusDays(30);
        List<OccasionReminder> upcomingNonYearlyOccasions =
                occasionReminderRepository.findByUserAndIsYearlyRecurringFalseAndOccasionDateBetween(
                        user, today, thirtyDaysLater);

        // Kết hợp cả hai danh sách
        List<OccasionReminder> allUpcomingOccasions = new ArrayList<>();
        allUpcomingOccasions.addAll(yearlyOccasions);
        allUpcomingOccasions.addAll(upcomingNonYearlyOccasions);

        // Sắp xếp theo ngày
        allUpcomingOccasions.sort((o1, o2) -> {
            LocalDate date1 = adjustDateForYearlyOccasion(o1);
            LocalDate date2 = adjustDateForYearlyOccasion(o2);
            return date1.compareTo(date2);
        });

        return allUpcomingOccasions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private LocalDate adjustDateForYearlyOccasion(OccasionReminder reminder) {
        LocalDate today = LocalDate.now();
        LocalDate occasionDate = reminder.getOccasionDate();

        if (reminder.isYearlyRecurring()) {
            // Điều chỉnh năm cho các dịp lặp lại hàng năm
            occasionDate = occasionDate.withYear(today.getYear());
            if (occasionDate.isBefore(today)) {
                occasionDate = occasionDate.withYear(today.getYear() + 1);
            }
        }

        return occasionDate;
    }

    @Scheduled(cron = "0 0 8 * * ?") // Run at 8:00 AM every day
    public void sendScheduledReminders() {
        log.info("Running scheduled occasion reminders check");

        // Lấy tất cả các nhắc nhở chưa được gửi
        List<OccasionReminder> allReminders = occasionReminderRepository.findByReminderSentFalse();
        List<OccasionReminder> remindersToSend = new ArrayList<>();

        LocalDate today = LocalDate.now();

        for (OccasionReminder reminder : allReminders) {
            LocalDate occasionDate = reminder.getOccasionDate();

            if (reminder.isYearlyRecurring()) {
                // Điều chỉnh năm cho các dịp lặp lại hàng năm
                occasionDate = occasionDate.withYear(today.getYear());
                if (occasionDate.isBefore(today)) {
                    occasionDate = occasionDate.withYear(today.getYear() + 1);
                }
            }

            // Kiểm tra xem hôm nay có phải là ngày cần gửi nhắc nhở không
            long daysUntilOccasion = ChronoUnit.DAYS.between(today, occasionDate);
            if (daysUntilOccasion == reminder.getReminderDaysBefore()) {
                remindersToSend.add(reminder);
            }
        }

        for (OccasionReminder reminder : remindersToSend) {
            try {
                // Send email reminder
                sendReminderEmail(reminder);

                // Mark as sent
                reminder.setReminderSent(true);
                occasionReminderRepository.save(reminder);

                log.info("Sent reminder for occasion: {}, user: {}",
                        reminder.getOccasionName(), reminder.getUser().getEmail());
            } catch (Exception e) {
                log.error("Failed to send reminder for occasion: {}, user: {}",
                        reminder.getOccasionName(), reminder.getUser().getEmail(), e);
            }
        }
    }

    // Reset reminder sent status for yearly recurring events at the start of each year
    @Scheduled(cron = "0 0 0 1 1 ?") // Run at midnight on January 1st
    public void resetYearlyRecurringReminders() {
        log.info("Resetting yearly recurring reminders");

        List<OccasionReminder> yearlyReminders = occasionReminderRepository.findAll().stream()
                .filter(OccasionReminder::isYearlyRecurring)
                .collect(Collectors.toList());

        for (OccasionReminder reminder : yearlyReminders) {
            reminder.setReminderSent(false);
            occasionReminderRepository.save(reminder);
        }

        log.info("Reset {} yearly recurring reminders", yearlyReminders.size());
    }

    private void sendReminderEmail(OccasionReminder reminder) throws MessagingException {
        User user = reminder.getUser();
        String recipientEmail = user.getEmail();
        String subject = "Nhắc nhở: " + reminder.getOccasionName() + " sắp đến!";

        LocalDate occasionDate = reminder.getOccasionDate();
        if (reminder.isYearlyRecurring()) {
            LocalDate today = LocalDate.now();
            occasionDate = occasionDate.withYear(today.getYear());
            if (occasionDate.isBefore(today)) {
                occasionDate = occasionDate.withYear(today.getYear() + 1);
            }
        }

        long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), occasionDate);

        StringBuilder emailContent = new StringBuilder();
        emailContent.append("<!DOCTYPE html>");
        emailContent.append("<html>");
        emailContent.append("<head>");
        emailContent.append("<meta charset=\"UTF-8\">");
        emailContent.append("<style>");
        emailContent.append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; }");
        emailContent.append(".header { background-color: #4a90e2; color: white; padding: 20px; text-align: center; border-radius: 5px 5px 0 0; }");
        emailContent.append(".content { padding: 20px; border: 1px solid #ddd; border-top: none; border-radius: 0 0 5px 5px; }");
        emailContent.append(".footer { text-align: center; margin-top: 20px; font-size: 12px; color: #777; }");
        emailContent.append(".highlight { font-weight: bold; color: #4a90e2; }");
        emailContent.append(".gift-ideas { background-color: #f9f9f9; padding: 15px; border-radius: 5px; margin-top: 20px; }");
        emailContent.append(".button { display: inline-block; background-color: #4a90e2; color: white; padding: 10px 15px; text-decoration: none; border-radius: 5px; }");
        emailContent.append("</style>");
        emailContent.append("</head>");
        emailContent.append("<body>");

        // Header
        emailContent.append("<div class=\"header\">");
        emailContent.append("<h1>Nhắc nhở dịp đặc biệt</h1>");
        emailContent.append("</div>");

        // Content
        emailContent.append("<div class=\"content\">");
        emailContent.append("<p>Xin chào ").append(user.getName()).append(",</p>");
        emailContent.append("<p>Chúng tôi muốn nhắc bạn rằng còn <span class=\"highlight\">").append(daysRemaining)
                .append(" ngày</strong> nữa là đến <span class=\"highlight\">").append(reminder.getOccasionName()).append("</span>");

        if (reminder.getRecipientName() != null && !reminder.getRecipientName().isEmpty()) {
            emailContent.append(" của <span class=\"highlight\">").append(reminder.getRecipientName()).append("</span>");
            if (reminder.getRelationship() != null && !reminder.getRelationship().isEmpty()) {
                emailContent.append(" (").append(reminder.getRelationship()).append(")");
            }
        }

        emailContent.append(" vào ngày <span class=\"highlight\">").append(occasionDate.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("</span>.</p>");

        // Gift preferences if available
        if (reminder.getGiftPreferences() != null && !reminder.getGiftPreferences().isEmpty()) {
            emailContent.append("<div class=\"gift-ideas\">");
            emailContent.append("<h3>Gợi ý quà tặng:</h3>");
            emailContent.append("<p>").append(reminder.getGiftPreferences()).append("</p>");
            emailContent.append("</div>");
        }

        emailContent.append("<p>Hãy chuẩn bị để làm cho ngày đặc biệt này thật ý nghĩa!</p>");

        emailContent.append("<p><a href=\"http://localhost:3000/profile/occasions\" class=\"button\">Quản lý dịp đặc biệt</a></p>");

        emailContent.append("<p>Trân trọng,<br>Đội ngũ Jewelry</p>");
        emailContent.append("</div>");

        // Footer
        emailContent.append("<div class=\"footer\">");
        emailContent.append("<p>Email này được gửi tự động từ hệ thống nhắc nhở dịp đặc biệt của Jewelry.</p>");
        emailContent.append("</div>");

        emailContent.append("</body>");
        emailContent.append("</html>");

        emailService.sendHtmlEmail(recipientEmail, subject, emailContent.toString());
    }

    private ResOccasionReminderDTO convertToDTO(OccasionReminder reminder) {
        ResOccasionReminderDTO dto = new ResOccasionReminderDTO();
        dto.setId(reminder.getId());
        dto.setOccasionName(reminder.getOccasionName());
        dto.setOccasionDate(reminder.getOccasionDate());
        dto.setReminderDaysBefore(reminder.getReminderDaysBefore());
        dto.setNotes(reminder.getNotes());
        dto.setYearlyRecurring(reminder.isYearlyRecurring());
        dto.setReminderSent(reminder.isReminderSent());
        dto.setRecipientName(reminder.getRecipientName());
        dto.setRelationship(reminder.getRelationship());
        dto.setGiftPreferences(reminder.getGiftPreferences());

        // Calculate days remaining
        LocalDate today = LocalDate.now();
        LocalDate occasionDate = adjustDateForYearlyOccasion(reminder);

        dto.setDaysRemaining(ChronoUnit.DAYS.between(today, occasionDate));

        // Không thêm gợi ý sản phẩm
        dto.setProductRecommendations(new ArrayList<>());

        return dto;
    }

    private List<Map<String, Object>> getProductRecommendations(OccasionReminder reminder) {
        // This would be implemented with actual product service integration
        // For now, return a placeholder
        List<Map<String, Object>> recommendations = new ArrayList<>();

        // Example structure for recommendations
        if ("Birthday".equalsIgnoreCase(reminder.getOccasionName())) {
            Map<String, Object> product = new HashMap<>();
            product.put("id", 1);
            product.put("name", "Birthstone Pendant");
            product.put("price", 129.99);
            product.put("imageUrl", "/images/products/birthstone-pendant.jpg");
            recommendations.add(product);
        } else if ("Anniversary".equalsIgnoreCase(reminder.getOccasionName())) {
            Map<String, Object> product = new HashMap<>();
            product.put("id", 2);
            product.put("name", "Diamond Anniversary Band");
            product.put("price", 599.99);
            product.put("imageUrl", "/images/products/anniversary-band.jpg");
            recommendations.add(product);
        }

        return recommendations;
    }
}