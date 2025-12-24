package com.selfhelp.tracker.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HabitLogRequest {
    private Long habitId;
    // ❌ REMOVED: private LocalDate date; (Now auto-generated as TODAY)
    private String status; // completed, skipped, failed
    private Integer currentStreak;
    private String moodAfter;
    private String notes;
}