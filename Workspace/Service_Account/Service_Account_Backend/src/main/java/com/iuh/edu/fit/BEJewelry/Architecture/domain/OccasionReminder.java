package com.iuh.edu.fit.BEJewelry.Architecture.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "occasion_reminders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OccasionReminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "Tên dịp đặc biệt không được để trống")
    @Column(nullable = false)
    private String occasionName;

    @NotNull(message = "Ngày diễn ra không được để trống")
    @Column(nullable = false)
    private LocalDate occasionDate;

    @Column(nullable = false)
    private Integer reminderDaysBefore = 7; // Default to 7 days

    @Column(length = 500)
    private String notes;

    // For recurring events like birthdays
    private boolean isYearlyRecurring = false;

    // For tracking if reminder has been sent for the current occurrence
    private boolean reminderSent = false;

    // For personalization
    @Column(length = 100)
    private String recipientName;

    @Column(length = 100)
    private String relationship;

    // For gift preferences
    @Column(length = 500)
    private String giftPreferences;
}