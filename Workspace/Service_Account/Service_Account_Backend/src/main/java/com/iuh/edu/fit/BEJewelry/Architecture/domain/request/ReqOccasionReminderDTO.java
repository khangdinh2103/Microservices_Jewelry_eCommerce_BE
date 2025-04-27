package com.iuh.edu.fit.BEJewelry.Architecture.domain.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReqOccasionReminderDTO {
    
    @NotBlank(message = "Tên dịp đặc biệt không được để trống")
    private String occasionName;
    
    @NotNull(message = "Ngày diễn ra không được để trống")
    private LocalDate occasionDate;
    
    private Integer reminderDaysBefore = 7;
    
    private String notes;
    
    private boolean isYearlyRecurring = false;
    
    private String recipientName;
    
    private String relationship;
    
    private String giftPreferences;
}