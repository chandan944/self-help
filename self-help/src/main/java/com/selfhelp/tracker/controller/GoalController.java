package com.selfhelp.tracker.controller;



import com.selfhelp.tracker.dto.GoalProgressRequest;
import com.selfhelp.tracker.dto.GoalRequest;
import com.selfhelp.tracker.model.Goal;
import com.selfhelp.tracker.model.GoalProgress;
import com.selfhelp.tracker.service.GoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<Goal> createGoal(
            @RequestBody GoalRequest req,
            @AuthenticationPrincipal String email
    ) {
        return ResponseEntity.ok(service.createGoal(req, email));
    }

    // Get All Goals for logged-in User
    @GetMapping("/me")
    public ResponseEntity<List<Goal>> getUserGoals(
            @AuthenticationPrincipal String email
    ) {
        return ResponseEntity.ok(service.getUserGoals(email));
    }

    // Get Single Goal
    @GetMapping("/{id}")
    public ResponseEntity<Goal> getGoal(
            @PathVariable Long id,
            @AuthenticationPrincipal String email
    ) {
        return ResponseEntity.ok(service.getGoal(id, email));
    }

    // Update Goal
    @PutMapping("/{id}")
    public ResponseEntity<Goal> updateGoal(
            @PathVariable Long id,
            @RequestBody GoalRequest req,
            @AuthenticationPrincipal String email
    ) {
        return ResponseEntity.ok(service.updateGoal(id, req, email));
    }

    // Delete Goal
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteGoal(
            @PathVariable Long id,
            @AuthenticationPrincipal String email
    ) {
        service.deleteGoal(id, email);
        return ResponseEntity.ok(Map.of("message", "Goal deleted successfully"));
    }

    // Log Daily Progress
    @PostMapping("/progress")
    public ResponseEntity<GoalProgress> logProgress(
            @RequestBody GoalProgressRequest req,
            @AuthenticationPrincipal String email
    ) {
        return ResponseEntity.ok(service.logProgress(req, email));
    }

    // Get Progress History
    @GetMapping("/{goalId}/progress")
    public ResponseEntity<List<GoalProgress>> getProgress(
            @PathVariable Long goalId,
            @RequestParam(required = false) Integer days,
            @AuthenticationPrincipal String email
    ) {
        return ResponseEntity.ok(service.getProgressHistory(goalId, days, email));
    }

    // Get Dashboard
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard(
            @AuthenticationPrincipal String email,
            @RequestParam(required = false) String date
    ) {
        LocalDate targetDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        return ResponseEntity.ok(service.getDashboard(email, targetDate));
    }
}