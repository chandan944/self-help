package com.selfhelp.todo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
class TodoStatsDto {
    private Long totalTodos;
    private Long completedTodos;
    private Long pendingTodos;
    private Long overdueTodos;
    private Long todayTodos;
    private Long weekTodos;
    private Double completionRate;
    private CategoryStatsDto categoryStats;
    private PriorityStatsDto priorityStats;
    private ProductivityStatsDto productivityStats;
}