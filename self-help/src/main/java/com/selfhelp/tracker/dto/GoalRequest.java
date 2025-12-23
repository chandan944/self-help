package com.selfhelp.tracker.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoalRequest {
    private Long userId;
    private String title;
    private LocalDate startDate;
    private LocalDate targetDate;
    private String priority;
    private String status;
    private String motivationReason;
}
