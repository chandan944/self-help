package com.selfhelp.todo;


import com.selfhelp.todo.*;
import com.selfhelp.todo.TodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/todos")
public class TodoController {
    private final TodoService todoService;

    @GetMapping
    public ResponseEntity<Page<TodoDto>> getAllTodos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(todoService.getAllTodos(email, page, size));
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<TodoDto>> getTodosByStatus(
            @RequestParam Boolean completed,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(todoService.getTodosByStatus(email, completed, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TodoDto> getTodo(
            @PathVariable Long id,
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(todoService.getTodo(id, email));
    }

    @PostMapping
    public ResponseEntity<TodoDto> createTodo(
            @Valid @RequestBody CreateTodoRequest request,
            @AuthenticationPrincipal String email) {
        TodoDto created = todoService.createTodo(request, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TodoDto> updateTodo(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTodoRequest request,
            @AuthenticationPrincipal String email) {
        TodoDto updated = todoService.updateTodo(id, request, email);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<TodoDto> toggleComplete(
            @PathVariable Long id,
            @RequestBody(required = false) CompleteTodoRequest request,
            @AuthenticationPrincipal String email) {
        TodoDto updated = todoService.toggleComplete(id, request, email);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(
            @PathVariable Long id,
            @AuthenticationPrincipal String email) {
        todoService.deleteTodo(id, email);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<TodoStatsDto> getStats(
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(todoService.getStats(email));
    }
}
