package com.selfhelp.tracker.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "goals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Goal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String title;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "target_date")
    private LocalDate targetDate;

    @Column(length = 20)
    private String priority; // Low, Medium, High

    @Column(length = 20)
    private String status = "in_progress"; // not_started, in_progress, paused, completed

    @Column(name = "motivation_reason", columnDefinition = "TEXT")
    private String motivationReason;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
