package com.selfhelp.tracker.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "habit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HabitLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "habit_id", nullable = false)
    private Long habitId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(length = 20)
    private String status; // completed, skipped, failed

    @Column(name = "current_streak")
    private Integer currentStreak = 0;

    @Column(name = "mood_after", length = 50)
    private String moodAfter;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}