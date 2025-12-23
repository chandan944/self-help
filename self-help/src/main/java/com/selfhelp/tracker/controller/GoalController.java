package com.selfhelp.tracker.controller;


import com.selfhelp.tracker.dto.GoalProgressRequest;
import com.selfhelp.tracker.dto.GoalRequest;
import com.selfhelp.tracker.model.Goal;
import com.selfhelp.tracker.model.GoalProgress;
import com.selfhelp.tracker.service.GoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService service;

    // Create Goal
    @PostMapping
    public ResponseEntity<Goal> createGoal(@RequestBody GoalRequest req) {
        return ResponseEntity.ok(service.createGoal(req));
    }

    // Get All Goals for User
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Goal>> getUserGoals(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getUserGoals(userId));
    }

    // Get Single Goal
    @GetMapping("/{id}")
    public ResponseEntity<Goal> getGoal(@PathVariable Long id) {
        return ResponseEntity.ok(service.getGoal(id));
    }

    // Update Goal
    @PutMapping("/{id}")
    public ResponseEntity<Goal> updateGoal(@PathVariable Long id, @RequestBody GoalRequest req) {
        return ResponseEntity.ok(service.updateGoal(id, req));
    }

    // Delete Goal
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteGoal(@PathVariable Long id) {
        service.deleteGoal(id);
        return ResponseEntity.ok(Map.of("message", "Goal deleted successfully"));
    }

    // Log Daily Progress
    @PostMapping("/progress")
    public ResponseEntity<GoalProgress> logProgress(@RequestBody GoalProgressRequest req) {
        return ResponseEntity.ok(service.logProgress(req));
    }

    // Get Progress History
    @GetMapping("/{goalId}/progress")
    public ResponseEntity<List<GoalProgress>> getProgress(
            @PathVariable Long goalId,
            @RequestParam(required = false) Integer days
    ) {
        return ResponseEntity.ok(service.getProgressHistory(goalId, days));
    }

    // Get Dashboard
    @GetMapping("/dashboard/{userId}")
    public ResponseEntity<Map<String, Object>> getDashboard(
            @PathVariable Long userId,
            @RequestParam(required = false) String date
    ) {
        LocalDate targetDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        return ResponseEntity.ok(service.getDashboard(userId, targetDate));
    }
}