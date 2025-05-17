package com.iuh.edu.fit.BEJewelry.Architecture.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import com.iuh.edu.fit.BEJewelry.Architecture.domain.OccasionReminder;
import com.iuh.edu.fit.BEJewelry.Architecture.domain.User;

@Repository
public interface OccasionReminderRepository extends JpaRepository<OccasionReminder, Long> {
    
    List<OccasionReminder> findByUser(User user);
    
    Page<OccasionReminder> findByUser(User user, Pageable pageable);
    
    List<OccasionReminder> findByUserAndIsYearlyRecurringTrue(User user);
    
    List<OccasionReminder> findByUserAndIsYearlyRecurringFalseAndOccasionDateBetween(
            User user, LocalDate startDate, LocalDate endDate);
    
    List<OccasionReminder> findByReminderSentFalse();
    
    // Thêm phương thức này vào OccasionReminderRepository
    List<OccasionReminder> findByReminderSentTrue();
    
    OccasionReminder findByUserAndId(User user, Long id);
}