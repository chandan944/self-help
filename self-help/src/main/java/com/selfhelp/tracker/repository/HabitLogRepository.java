package com.selfhelp.tracker.repository;

import com.selfhelp.tracker.model.HabitLog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HabitLogRepository extends JpaRepository<HabitLog, Long> {

    // Find all logs for a habit
    List<HabitLog> findByHabitId(Long habitId);

    // Find log for specific habit and date
    Optional<HabitLog> findByHabitIdAndDate(Long habitId, LocalDate date);

    // Find logs between dates - OPTION 1 (Spring Data JPA naming)
    List<HabitLog> findByHabitIdAndDateBetween(Long habitId, LocalDate startDate, LocalDate endDate);

    // Find logs between dates - OPTION 2 (Custom Query - Use this if Option 1 fails)
    @Query("SELECT hl FROM HabitLog hl WHERE hl.habitId = :habitId AND hl.date >= :startDate AND hl.date <= :endDate ORDER BY hl.date DESC")
    List<HabitLog> findLogsInDateRange(@Param("habitId") Long habitId,
                                       @Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate);

    // Delete all logs for a habit (useful for cleanup)
    void deleteByHabitId(Long habitId);
}