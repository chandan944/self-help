package com.selfhelp.todo;


import com.selfhelp.todo.*;
import com.selfhelp.todo.Todo;
import com.selfhelp.todo.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TodoService {
    private final TodoRepository todoRepository;

    @Transactional(readOnly = true)
    public Page<TodoDto> getAllTodos(String userEmail, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return todoRepository.findByUserEmailOrderByCreatedAtDesc(userEmail, pageable)
                .map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public Page<TodoDto> getTodosByStatus(String userEmail, Boolean completed, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return todoRepository.findByUserEmailAndCompletedOrderByCreatedAtDesc(
                userEmail, completed, pageable).map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public TodoDto getTodo(Long id, String userEmail) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        if (!todo.getUserEmail().equals(userEmail)) {
            throw new RuntimeException("Unauthorized access");
        }

        return convertToDto(todo);
    }

    @Transactional
    public TodoDto createTodo(CreateTodoRequest request, String userEmail) {
        Todo todo = new Todo();
        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        todo.setPriority(request.getPriority() != null ? request.getPriority() : Todo.Priority.MEDIUM);
        todo.setCategory(request.getCategory() != null ? request.getCategory() : Todo.Category.PERSONAL);
        todo.setDueDate(request.getDueDate());
        todo.setEstimatedMinutes(request.getEstimatedMinutes());
        todo.setTags(request.getTags());
        todo.setUserEmail(userEmail);

        Todo saved = todoRepository.save(todo);
        return convertToDto(saved);
    }

    @Transactional
    public TodoDto updateTodo(Long id, UpdateTodoRequest request, String userEmail) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        if (!todo.getUserEmail().equals(userEmail)) {
            throw new RuntimeException("Unauthorized access");
        }

        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        todo.setPriority(request.getPriority());
        todo.setCategory(request.getCategory());
        todo.setDueDate(request.getDueDate());
        todo.setEstimatedMinutes(request.getEstimatedMinutes());
        todo.setTags(request.getTags());

        Todo updated = todoRepository.save(todo);
        return convertToDto(updated);
    }

    @Transactional
    public TodoDto toggleComplete(Long id, CompleteTodoRequest request, String userEmail) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        if (!todo.getUserEmail().equals(userEmail)) {
            throw new RuntimeException("Unauthorized access");
        }

        todo.setCompleted(!todo.getCompleted());

        if (todo.getCompleted()) {
            todo.setCompletedAt(LocalDateTime.now());
            if (request != null && request.getActualMinutes() != null) {
                todo.setActualMinutes(request.getActualMinutes());
            }
        } else {
            todo.setCompletedAt(null);
            todo.setActualMinutes(null);
        }

        Todo updated = todoRepository.save(todo);
        return convertToDto(updated);
    }

    @Transactional
    public void deleteTodo(Long id, String userEmail) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        if (!todo.getUserEmail().equals(userEmail)) {
            throw new RuntimeException("Unauthorized access");
        }

        todoRepository.delete(todo);
    }

    @Transactional(readOnly = true)
    public TodoStatsDto getStats(String userEmail) {
        List<Todo> allTodos = todoRepository.findByUserEmailAndCompleted(userEmail, false);
        allTodos.addAll(todoRepository.findByUserEmailAndCompleted(userEmail, true));

        long total = allTodos.size();
        long completed = allTodos.stream().filter(Todo::getCompleted).count();
        long pending = total - completed;

        LocalDate today = LocalDate.now();
        long overdue = allTodos.stream()
                .filter(t -> !t.getCompleted() && t.getDueDate() != null && t.getDueDate().isBefore(today))
                .count();

        long todayTodos = allTodos.stream()
                .filter(t -> t.getDueDate() != null && t.getDueDate().equals(today))
                .count();

        LocalDate weekEnd = today.plusDays(7);
        long weekTodos = allTodos.stream()
                .filter(t -> t.getDueDate() != null &&
                        !t.getDueDate().isBefore(today) &&
                        !t.getDueDate().isAfter(weekEnd))
                .count();

        double completionRate = total > 0 ? (completed * 100.0 / total) : 0.0;

        TodoStatsDto stats = new TodoStatsDto();
        stats.setTotalTodos(total);
        stats.setCompletedTodos(completed);
        stats.setPendingTodos(pending);
        stats.setOverdueTodos(overdue);
        stats.setTodayTodos(todayTodos);
        stats.setWeekTodos(weekTodos);
        stats.setCompletionRate(Math.round(completionRate * 10) / 10.0);
        stats.setCategoryStats(calculateCategoryStats(allTodos));
        stats.setPriorityStats(calculatePriorityStats(allTodos));
        stats.setProductivityStats(calculateProductivityStats(userEmail, allTodos));

        return stats;
    }

    private CategoryStatsDto calculateCategoryStats(List<Todo> todos) {
        Map<Todo.Category, Long> counts = todos.stream()
                .collect(Collectors.groupingBy(Todo::getCategory, Collectors.counting()));

        CategoryStatsDto stats = new CategoryStatsDto();
        stats.setPersonal(counts.getOrDefault(Todo.Category.PERSONAL, 0L));
        stats.setWork(counts.getOrDefault(Todo.Category.WORK, 0L));
        stats.setHealth(counts.getOrDefault(Todo.Category.HEALTH, 0L));
        stats.setLearning(counts.getOrDefault(Todo.Category.LEARNING, 0L));
        stats.setShopping(counts.getOrDefault(Todo.Category.SHOPPING, 0L));
        stats.setOther(counts.getOrDefault(Todo.Category.OTHER, 0L));
        return stats;
    }

    private PriorityStatsDto calculatePriorityStats(List<Todo> todos) {
        Map<Todo.Priority, Long> counts = todos.stream()
                .filter(t -> !t.getCompleted())
                .collect(Collectors.groupingBy(Todo::getPriority, Collectors.counting()));

        PriorityStatsDto stats = new PriorityStatsDto();
        stats.setLow(counts.getOrDefault(Todo.Priority.LOW, 0L));
        stats.setMedium(counts.getOrDefault(Todo.Priority.MEDIUM, 0L));
        stats.setHigh(counts.getOrDefault(Todo.Priority.HIGH, 0L));
        stats.setUrgent(counts.getOrDefault(Todo.Priority.URGENT, 0L));
        return stats;
    }

    private ProductivityStatsDto calculateProductivityStats(String userEmail, List<Todo> allTodos) {
        LocalDateTime now = LocalDateTime.now();

        long completedToday = todoRepository.countCompletedSince(
                userEmail, now.truncatedTo(ChronoUnit.DAYS));
        long completedWeek = todoRepository.countCompletedSince(
                userEmail, now.minusDays(7));
        long completedMonth = todoRepository.countCompletedSince(
                userEmail, now.minusDays(30));

        OptionalDouble avgTime = allTodos.stream()
                .filter(t -> t.getCompleted() && t.getActualMinutes() != null)
                .mapToInt(Todo::getActualMinutes)
                .average();

        List<DailyProductivityDto> last7Days = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(23, 59, 59);

            long completedOnDay = allTodos.stream()
                    .filter(t -> t.getCompletedAt() != null &&
                            !t.getCompletedAt().isBefore(startOfDay) &&
                            !t.getCompletedAt().isAfter(endOfDay))
                    .count();

            long createdOnDay = allTodos.stream()
                    .filter(t -> !t.getCreatedAt().isBefore(startOfDay) &&
                            !t.getCreatedAt().isAfter(endOfDay))
                    .count();

            last7Days.add(new DailyProductivityDto(date, completedOnDay, createdOnDay));
        }

        ProductivityStatsDto stats = new ProductivityStatsDto();
        stats.setCompletedToday(completedToday);
        stats.setCompletedThisWeek(completedWeek);
        stats.setCompletedThisMonth(completedMonth);
        stats.setAvgCompletionTime(avgTime.isPresent() ? (int) avgTime.getAsDouble() : 0);
        stats.setLast7Days(last7Days);

        return stats;
    }

    private TodoDto convertToDto(Todo todo) {
        TodoDto dto = new TodoDto();
        dto.setId(todo.getId());
        dto.setTitle(todo.getTitle());
        dto.setDescription(todo.getDescription());
        dto.setPriority(todo.getPriority());
        dto.setCategory(todo.getCategory());
        dto.setCompleted(todo.getCompleted());
        dto.setDueDate(todo.getDueDate());
        dto.setCompletedAt(todo.getCompletedAt());
        dto.setUserEmail(todo.getUserEmail());
        dto.setCreatedAt(todo.getCreatedAt());
        dto.setUpdatedAt(todo.getUpdatedAt());
        dto.setEstimatedMinutes(todo.getEstimatedMinutes());
        dto.setActualMinutes(todo.getActualMinutes());
        dto.setTags(todo.getTags());

        boolean isOverdue = !todo.getCompleted() &&
                todo.getDueDate() != null &&
                todo.getDueDate().isBefore(LocalDate.now());
        dto.setIsOverdue(isOverdue);

        return dto;
    }
}
