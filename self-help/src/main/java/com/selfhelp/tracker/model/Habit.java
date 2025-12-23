package com.selfhelp.tracker.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "habits")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Habit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String title;

    @Column(name = "target_value")
    private String targetValue;

    @Column(name = "best_streak")
    private Integer bestStreak = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}

