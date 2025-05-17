package com.iuh.edu.fit.BEJewelry.Architecture.domain.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ResOccasionReminderDTO {

    private Long id;
    private String occasionName;
    private LocalDate occasionDate;
    private Integer reminderDaysBefore;
    private String notes;
    private boolean isYearlyRecurring;
    private boolean reminderSent;
    private String recipientName;
    private String relationship;
    private String giftPreferences;

    // Calculate days remaining until the occasion
    private Long daysRemaining;

    // For upcoming occasions, include product recommendations
    private Object productRecommendations;
}