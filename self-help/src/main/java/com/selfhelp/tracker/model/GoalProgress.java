package com.selfhelp.tracker.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "goal_progress")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoalProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "goal_id", nullable = false)
    private Long goalId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "today_progress")
    private Integer todayProgress = 0;

    @Column(name = "total_progress")
    private Integer totalProgress = 0;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
