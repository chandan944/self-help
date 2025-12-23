package com.selfhelp.tracker.dto;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HabitRequest {
    private Long userId;
    private String title;
    private String targetValue;
}
