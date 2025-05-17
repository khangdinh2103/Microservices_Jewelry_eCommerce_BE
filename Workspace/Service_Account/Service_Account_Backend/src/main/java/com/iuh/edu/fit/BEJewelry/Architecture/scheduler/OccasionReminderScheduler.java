package com.iuh.edu.fit.BEJewelry.Architecture.scheduler;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.iuh.edu.fit.BEJewelry.Architecture.domain.OccasionReminder;
import com.iuh.edu.fit.BEJewelry.Architecture.domain.User;
import com.iuh.edu.fit.BEJewelry.Architecture.repository.OccasionReminderRepository;
import com.iuh.edu.fit.BEJewelry.Architecture.service.EmailService;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OccasionReminderScheduler {

    private final OccasionReminderRepository occasionReminderRepository;
    private final EmailService emailService;
    
    // Chạy mỗi ngày vào lúc 8:00 sáng
    // For testing purposes, run every minute
    @Scheduled(cron = "0 * * * * ?")
    @Transactional
    public void sendOccasionReminders() {
        log.info("Bắt đầu kiểm tra và gửi nhắc nhở dịp đặc biệt...");
        
        LocalDate today = LocalDate.now();
        log.info("Ngày hiện tại: {}", today);
        
        List<OccasionReminder> reminders = occasionReminderRepository.findByReminderSentFalse();
        log.info("Tìm thấy {} nhắc nhở chưa gửi", reminders.size());
        
        for (OccasionReminder reminder : reminders) {
            try {
                LocalDate occasionDate = reminder.getOccasionDate();
                log.info("Xử lý nhắc nhở: {}, ngày: {}, nhắc trước: {} ngày, lặp lại hàng năm: {}", 
                        reminder.getOccasionName(), occasionDate, reminder.getReminderDaysBefore(), 
                        reminder.isYearlyRecurring());
                
                // Nếu là dịp lặp lại hàng năm, điều chỉnh năm
                if (reminder.isYearlyRecurring()) {
                    // Điều chỉnh năm cho dịp lặp lại hàng năm
                    LocalDate adjustedDate = LocalDate.of(
                        today.getYear(),
                        occasionDate.getMonth(),
                        occasionDate.getDayOfMonth()
                    );
                    
                    // Nếu ngày đã qua trong năm nay, sử dụng năm sau
                    if (adjustedDate.isBefore(today)) {
                        adjustedDate = adjustedDate.plusYears(1);
                    }
                    
                    occasionDate = adjustedDate;
                    log.info("Ngày sau khi điều chỉnh: {}", occasionDate);
                }
                
                // Tính số ngày còn lại
                long daysUntilOccasion = ChronoUnit.DAYS.between(today, occasionDate);
                log.info("Số ngày còn lại đến dịp: {}", daysUntilOccasion);
                
                // Kiểm tra xem có phải ngày cần gửi nhắc nhở không
                if (daysUntilOccasion == reminder.getReminderDaysBefore()) {
                    User user = reminder.getUser();
                    log.info("Cần gửi nhắc nhở cho người dùng: {}", user.getEmail());
                    
                    // Gửi email
                    emailService.sendOccasionReminderEmail(
                        user.getEmail(),
                        user.getName(),
                        reminder.getOccasionName(),
                        occasionDate,
                        reminder.getRecipientName(),
                        reminder.getRelationship(),
                        reminder.getGiftPreferences()
                    );
                    
                    // Cập nhật trạng thái đã gửi
                    reminder.setReminderSent(true);
                    occasionReminderRepository.save(reminder);
                    
                    log.info("Đã gửi nhắc nhở cho dịp {} của người dùng {}", 
                            reminder.getOccasionName(), user.getEmail());
                } else {
                    log.info("Chưa đến ngày gửi nhắc nhở (cần gửi trước {} ngày, còn lại {} ngày)", 
                            reminder.getReminderDaysBefore(), daysUntilOccasion);
                }
            } catch (MessagingException e) {
                log.error("Lỗi khi gửi email nhắc nhở: {}", e.getMessage(), e);
            } catch (Exception e) {
                log.error("Lỗi không xác định khi xử lý nhắc nhở: {}", e.getMessage(), e);
            }
        }
        
        // Đặt lại trạng thái reminderSent cho các dịp lặp lại hàng năm đã qua
        resetYearlyRecurringReminders();
        
        log.info("Hoàn thành gửi nhắc nhở dịp đặc biệt");
    }
    
    /**
     * Đặt lại trạng thái reminderSent cho các dịp lặp lại hàng năm đã qua
     */
    private void resetYearlyRecurringReminders() {
        LocalDate today = LocalDate.now();
        List<OccasionReminder> yearlyReminders = occasionReminderRepository.findByReminderSentTrue();
        
        for (OccasionReminder reminder : yearlyReminders) {
            if (reminder.isYearlyRecurring()) {
                LocalDate occasionDate = reminder.getOccasionDate();
                LocalDate thisYearDate = LocalDate.of(
                    today.getYear(),
                    occasionDate.getMonth(),
                    occasionDate.getDayOfMonth()
                );
                
                // Nếu ngày trong năm nay đã qua, đặt lại trạng thái reminderSent
                if (thisYearDate.isBefore(today)) {
                    reminder.setReminderSent(false);
                    occasionReminderRepository.save(reminder);
                    log.info("Đặt lại trạng thái nhắc nhở cho dịp lặp lại hàng năm: {}", reminder.getOccasionName());
                }
            }
        }
    }
}