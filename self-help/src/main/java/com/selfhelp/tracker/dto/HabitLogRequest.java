package com.selfhelp.tracker.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HabitLogRequest {
    private Long habitId;
    private LocalDate date;
    private String status;
    private Integer currentStreak;
    private String moodAfter;
    private String notes;
}
