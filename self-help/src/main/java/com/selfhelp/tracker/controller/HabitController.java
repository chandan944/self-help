package com.selfhelp.tracker.controller;


import com.selfhelp.tracker.dto.HabitLogRequest;
import com.selfhelp.tracker.dto.HabitRequest;
import com.selfhelp.tracker.model.Habit;
import com.selfhelp.tracker.model.HabitLog;
import com.selfhelp.tracker.service.HabitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Habit> createHabit(@RequestBody HabitRequest req) {
        return ResponseEntity.ok(service.createHabit(req));
    }

    // Get All Habits for User
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Habit>> getUserHabits(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getUserHabits(userId));
    }

    // Get Single Habit
    @GetMapping("/{id}")
    public ResponseEntity<Habit> getHabit(@PathVariable Long id) {
        return ResponseEntity.ok(service.getHabit(id));
    }

    // Update Habit
    @PutMapping("/{id}")
    public ResponseEntity<Habit> updateHabit(@PathVariable Long id, @RequestBody HabitRequest req) {
        return ResponseEntity.ok(service.updateHabit(id, req));
    }

    // Delete Habit
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteHabit(@PathVariable Long id) {
        service.deleteHabit(id);
        return ResponseEntity.ok(Map.of("message", "Habit deleted successfully"));
    }

    // Log Daily Habit
    @PostMapping("/log")
    public ResponseEntity<HabitLog> logHabit(@RequestBody HabitLogRequest req) {
        return ResponseEntity.ok(service.logHabit(req));
    }

    // Get Habit History
    @GetMapping("/{habitId}/logs")
    public ResponseEntity<List<HabitLog>> getHistory(
            @PathVariable Long habitId,
            @RequestParam(required = false) Integer days
    ) {
        return ResponseEntity.ok(service.getHabitHistory(habitId, days));
    }

    // Get Today's Habits
    @GetMapping("/today/{userId}")
    public ResponseEntity<Map<String, Object>> getTodayHabits(
            @PathVariable Long userId,
            @RequestParam(required = false) String date
    ) {
        LocalDate targetDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        return ResponseEntity.ok(service.getTodayHabits(userId, targetDate));
    }
}