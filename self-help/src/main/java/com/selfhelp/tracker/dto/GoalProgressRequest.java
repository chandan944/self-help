package com.selfhelp.tracker.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoalProgressRequest {
    private Long goalId;
    // ❌ REMOVED: private LocalDate date;  (Now auto-generated)
    private Integer todayProgress;
    private Integer totalProgress;
    private String notes;
}
