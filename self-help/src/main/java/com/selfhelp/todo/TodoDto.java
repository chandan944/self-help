package com.selfhelp.todo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;





// ============== RESPONSE DTOs ==============

@Data
@NoArgsConstructor
@AllArgsConstructor
class TodoDto {
    private Long id;
    private String title;
    private String description;
    private Todo.Priority priority;
    private Todo.Category category;
    private Boolean completed;
    private LocalDate dueDate;
    private LocalDateTime completedAt;
    private String userEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer estimatedMinutes;
    private Integer actualMinutes;
    private String tags;
    private Boolean isOverdue;
}


@Data
@NoArgsConstructor
@AllArgsConstructor
class CategoryStatsDto {
    private Long personal;
    private Long work;
    private Long health;
    private Long learning;
    private Long shopping;
    private Long other;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class PriorityStatsDto {
    private Long low;
    private Long medium;
    private Long high;
    private Long urgent;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class ProductivityStatsDto {
    private Long completedToday;
    private Long completedThisWeek;
    private Long completedThisMonth;
    private Integer avgCompletionTime;
    private List<DailyProductivityDto> last7Days;
}


