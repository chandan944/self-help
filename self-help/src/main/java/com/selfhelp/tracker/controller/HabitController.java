package com.selfhelp.tracker.controller;

import com.selfhelp.tracker.dto.*;
import com.selfhelp.tracker.model.*;
import com.selfhelp.tracker.service.HabitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/habits")
@RequiredArgsConstructor
public class HabitController {

    private final HabitService service;

    // Create Habit
    @PostMapping
    public ResponseEntity<Habit> createHabit(
            @RequestBody HabitRequest req,
            @AuthenticationPrincipal String email
    ) {
        return ResponseEntity.ok(service.createHabit(req, email));
    }

    // Get All Habits for logged-in User
    @GetMapping("/me")
    public ResponseEntity<List<Habit>> getUserHabits(
            @AuthenticationPrincipal String email
    ) {
        return ResponseEntity.ok(service.getUserHabits(email));
    }

    // Get Single Habit
    @GetMapping("/{id}")
    public ResponseEntity<Habit> getHabit(
            @PathVariable Long id,
            @AuthenticationPrincipal String email
    ) {
        return ResponseEntity.ok(service.getHabit(id, email));
    }

    // Update Habit
    @PutMapping("/{id}")
    public ResponseEntity<Habit> updateHabit(
            @PathVariable Long id,
            @RequestBody HabitRequest req,
            @AuthenticationPrincipal String email
    ) {
        return ResponseEntity.ok(service.updateHabit(id, req, email));
    }

    // Delete Habit
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteHabit(
            @PathVariable Long id,
            @AuthenticationPrincipal String email
    ) {
        service.deleteHabit(id, email);
        return ResponseEntity.ok(Map.of("message", "Habit deleted successfully"));
    }

    // ✅ Log Today's Habit - DATE IS AUTO-GENERATED
    @PostMapping("/log")
    public ResponseEntity<HabitLog> logHabit(
            @RequestBody HabitLogRequest req,
            @AuthenticationPrincipal String email
    ) {
        return ResponseEntity.ok(service.logHabit(req, email));
    }

    // Get Habit History
    @GetMapping("/{habitId}/logs")
    public ResponseEntity<List<HabitLog>> getHistory(
            @PathVariable Long habitId,
            @RequestParam(required = false) Integer days,
            @AuthenticationPrincipal String email
    ) {
        return ResponseEntity.ok(service.getHabitHistory(habitId, days, email));
    }

    // Get Today's Habits (with optional date)
    @GetMapping("/today")
    public ResponseEntity<Map<String, Object>> getTodayHabits(
            @AuthenticationPrincipal String email,
            @RequestParam(required = false) String date
    ) {
        LocalDate targetDate = (date != null) ? LocalDate.parse(date) : LocalDate.now();
        return ResponseEntity.ok(service.getTodayHabits(email, targetDate));
    }

    // Get Today's Habits (Simplified - always uses current date)
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getHabitDashboard(
            @AuthenticationPrincipal String email
    ) {
        return ResponseEntity.ok(service.getTodayHabits(email));
    }

    // Debug endpoint: Get all logs for a habit
    @GetMapping("/{habitId}/logs/all")
    public ResponseEntity<List<HabitLog>> getAllLogs(
            @PathVariable Long habitId,
            @AuthenticationPrincipal String email
    ) {
        return ResponseEntity.ok(service.getAllLogsForHabit(habitId, email));
    }

    // Debug endpoint: Check database state
    @GetMapping("/{habitId}/debug")
    public ResponseEntity<Map<String, Object>> debugHabitData(
            @PathVariable Long habitId,
            @AuthenticationPrincipal String email
    ) {
        return ResponseEntity.ok(service.debugHabitData(habitId, email));
    }
}