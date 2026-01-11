package com.selfhelp.todo;

import com.selfhelp.todo.Todo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;

@Data
public class UpdateTodoRequest {
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must be less than 200 characters")
    private String title;

    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;

    private Todo.Priority priority;

    private Todo.Category category;

    private LocalDate dueDate;

    private Integer estimatedMinutes;

    private String tags;
}
