package com.selfhelp.tracker.service;

import com.selfhelp.tracker.dto.*;
import com.selfhelp.tracker.exception.ResourceNotFoundException;
import com.selfhelp.tracker.model.*;
import com.selfhelp.tracker.repository.*;
import com.selfhelp.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HabitService {

    private final HabitRepository habitRepo;
    private final HabitLogRepository logRepo;
    private final UserRepository userRepo;

    // Helper method to get user by email
    private Long getUserIdByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"))
                .getId();
    }

    // Create Habit
    @Transactional
    public Habit createHabit(HabitRequest req, String email) {
        Long userId = getUserIdByEmail(email);

        Habit habit = new Habit();
        habit.setUserId(userId);
        habit.setTitle(req.getTitle());
        habit.setTargetValue(req.getTargetValue());
        habit.setBestStreak(0);
        return habitRepo.save(habit);
    }

    // Get All Habits for logged-in User
    public List<Habit> getUserHabits(String email) {
        Long userId = getUserIdByEmail(email);
        return habitRepo.findByUserId(userId);
    }

    // Get Single Habit - Verify ownership
    public Habit getHabit(Long id, String email) {
        Long userId = getUserIdByEmail(email);
        Habit habit = habitRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Habit not found with id: " + id));

        if (!habit.getUserId().equals(userId)) {
            throw new AccessDeniedException("You don't have permission to access this habit");
        }
        return habit;
    }

    // Update Habit - Verify ownership
    @Transactional
    public Habit updateHabit(Long id, HabitRequest req, String email) {
        Habit habit = getHabit(id, email);
        if (req.getTitle() != null) habit.setTitle(req.getTitle());
        if (req.getTargetValue() != null) habit.setTargetValue(req.getTargetValue());
        return habitRepo.save(habit);
    }

    // Delete Habit - Verify ownership
    @Transactional
    public void deleteHabit(Long id, String email) {
        Habit habit = getHabit(id, email);
        habitRepo.delete(habit);
    }

    // ✅ Log Daily Habit - DATE IS AUTO-GENERATED (TODAY)
    @Transactional
    public HabitLog logHabit(HabitLogRequest req, String email) {
        Habit habit = getHabit(req.getHabitId(), email);

        // ✅ Auto-generate today's date
        LocalDate today = LocalDate.now();

        // Check if already logged TODAY
        Optional<HabitLog> existing = logRepo.findByHabitIdAndDate(
                req.getHabitId(), today
        );

        HabitLog log;
        if (existing.isPresent()) {
            // Update existing entry for TODAY
            log = existing.get();
            log.setStatus(req.getStatus());
            log.setCurrentStreak(req.getCurrentStreak());
            log.setMoodAfter(req.getMoodAfter());
            log.setNotes(req.getNotes());
            System.out.println("✅ Updated habit log for date: " + today);
        } else {
            // Create new entry for TODAY
            log = new HabitLog();
            log.setHabitId(req.getHabitId());
            log.setDate(today); // ✅ AUTO-GENERATED
            log.setStatus(req.getStatus());
            log.setCurrentStreak(req.getCurrentStreak());
            log.setMoodAfter(req.getMoodAfter());
            log.setNotes(req.getNotes());
            System.out.println("✅ Created new habit log for date: " + today);
        }

        // Update best streak if needed
        if (req.getCurrentStreak() != null && req.getCurrentStreak() > habit.getBestStreak()) {
            habit.setBestStreak(req.getCurrentStreak());
            habitRepo.save(habit);
            System.out.println("🏆 New best streak: " + req.getCurrentStreak());
        }

        return logRepo.save(log);
    }

    // Get Habit History - FIXED VERSION with multiple fallback methods
    public List<HabitLog> getHabitHistory(Long habitId, Integer days, String email) {
        getHabit(habitId, email);

        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(days != null ? days : 7);

        System.out.println("🔍 Fetching habit logs for habitId: " + habitId);
        System.out.println("📅 Date Range: " + startDate + " to " + today);
        System.out.println("📅 Days requested: " + (days != null ? days : 7));

        List<HabitLog> results = null;

        // METHOD 1: Try the standard BETWEEN query
        try {
            results = logRepo.findByHabitIdAndDateBetween(habitId, startDate, today);
            System.out.println("✅ Method 1 (findByHabitIdAndDateBetween): Found " + results.size() + " entries");
        } catch (Exception e) {
            System.out.println("❌ Method 1 failed: " + e.getMessage());
        }

        // METHOD 2: If Method 1 returns empty or fails, try custom query
        if (results == null || results.isEmpty()) {
            try {
                results = logRepo.findLogsInDateRange(habitId, startDate, today);
                System.out.println("✅ Method 2 (findLogsInDateRange): Found " + results.size() + " entries");
            } catch (Exception e) {
                System.out.println("❌ Method 2 failed: " + e.getMessage());
            }
        }

        // METHOD 3: If both fail, manual filter
        if (results == null || results.isEmpty()) {
            System.out.println("⚠️ Both methods returned empty. Trying manual filter...");
            List<HabitLog> allLogs = logRepo.findByHabitId(habitId);
            System.out.println("📊 Total logs in DB for this habit: " + allLogs.size());

            if (!allLogs.isEmpty()) {
                System.out.println("📅 All available dates:");
                allLogs.forEach(l -> System.out.println("   - " + l.getDate() + " (Status: " + l.getStatus() + ", Streak: " + l.getCurrentStreak() + ")"));

                // Manual filter
                final LocalDate finalStartDate = startDate;
                final LocalDate finalEndDate = today;
                results = allLogs.stream()
                        .filter(log -> !log.getDate().isBefore(finalStartDate) && !log.getDate().isAfter(finalEndDate))
                        .sorted((a, b) -> b.getDate().compareTo(a.getDate())) // Sort by date DESC
                        .collect(Collectors.toList());

                System.out.println("✅ Method 3 (Manual filter): Found " + results.size() + " entries in range");
            } else {
                System.out.println("❌ No habit logs exist for this habit at all");
                results = new ArrayList<>();
            }
        }

        System.out.println("📊 Final result count: " + results.size());
        return results;
    }

    // Get Today's Habits - Only for authenticated user
    public Map<String, Object> getTodayHabits(String email, LocalDate date) {
        Long userId = getUserIdByEmail(email);

        // If no date provided, use today
        LocalDate targetDate = (date != null) ? date : LocalDate.now();

        System.out.println("🔍 Fetching habits for userId: " + userId + " on date: " + targetDate);

        List<Habit> habits = habitRepo.findByUserId(userId);
        System.out.println("📊 Found " + habits.size() + " habits for user");

        List<Map<String, Object>> habitData = new ArrayList<>();

        for (Habit habit : habits) {
            Map<String, Object> data = new HashMap<>();
            data.put("habit", habit);

            Optional<HabitLog> todayLog = logRepo.findByHabitIdAndDate(habit.getId(), targetDate);
            data.put("todayLog", todayLog.orElse(null));

            System.out.println("   - Habit: " + habit.getTitle() + ", Log present: " + todayLog.isPresent());

            habitData.add(data);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("date", targetDate);
        response.put("habits", habitData);
        return response;
    }

    // Get Today's Habits (Simplified)
    public Map<String, Object> getTodayHabits(String email) {
        return getTodayHabits(email, LocalDate.now());
    }

    // DEBUGGING: Get all logs for a habit
    public List<HabitLog> getAllLogsForHabit(Long habitId, String email) {
        getHabit(habitId, email);
        List<HabitLog> allLogs = logRepo.findByHabitId(habitId);
        System.out.println("📊 Total habit logs: " + allLogs.size());

        if (!allLogs.isEmpty()) {
            System.out.println("📅 All dates in database:");
            allLogs.forEach(log ->
                    System.out.println("   - ID: " + log.getId() +
                            ", Date: " + log.getDate() +
                            ", Status: " + log.getStatus() +
                            ", Streak: " + log.getCurrentStreak())
            );
        }

        return allLogs;
    }

    // DEBUGGING: Check database state
    public Map<String, Object> debugHabitData(Long habitId, String email) {
        getHabit(habitId, email);

        Map<String, Object> debug = new HashMap<>();

        // Get all logs
        List<HabitLog> allLogs = logRepo.findByHabitId(habitId);
        debug.put("totalLogs", allLogs.size());

        if (!allLogs.isEmpty()) {
            debug.put("firstDate", allLogs.stream().map(HabitLog::getDate).min(LocalDate::compareTo).orElse(null));
            debug.put("lastDate", allLogs.stream().map(HabitLog::getDate).max(LocalDate::compareTo).orElse(null));
            debug.put("allDates", allLogs.stream().map(HabitLog::getDate).collect(Collectors.toList()));
        }

        // Check today
        LocalDate today = LocalDate.now();
        Optional<HabitLog> todayLog = logRepo.findByHabitIdAndDate(habitId, today);
        debug.put("todayLogExists", todayLog.isPresent());
        debug.put("todayDate", today);

        // Try date range
        LocalDate startDate = today.minusDays(7);
        List<HabitLog> rangeResults = logRepo.findByHabitIdAndDateBetween(habitId, startDate, today);
        debug.put("last7DaysCount", rangeResults.size());
        debug.put("queryStartDate", startDate);
        debug.put("queryEndDate", today);

        return debug;
    }
}