package com.selfhelp.tracker.service;

import com.selfhelp.tracker.dto.*;
import com.selfhelp.tracker.exception.ResourceNotFoundException;
import com.selfhelp.tracker.model.*;
import com.selfhelp.tracker.repository.GoalRepository;
import com.selfhelp.tracker.repository.*;
import com.selfhelp.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.*;
@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepo;
    private final GoalProgressRepository progressRepo;
    private final UserRepository userRepo;

    // Helper method to get userId from email
    private Long getUserIdByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email))
                .getId();
    }

    // Create Goal - Only for authenticated user
    @Transactional
    public Goal createGoal(GoalRequest req, String email) {
        Long userId = getUserIdByEmail(email);

        Goal goal = new Goal();
        goal.setUserId(userId);
        goal.setTitle(req.getTitle());
        goal.setStartDate(req.getStartDate());
        goal.setTargetDate(req.getTargetDate());
        goal.setPriority(req.getPriority());
        goal.setStatus(req.getStatus() != null ? req.getStatus() : "in_progress");
        goal.setMotivationReason(req.getMotivationReason());
        return goalRepo.save(goal);
    }

    // Get All Goals - Only for authenticated user
    public List<Goal> getUserGoals(String email) {
        Long userId = getUserIdByEmail(email);
        return goalRepo.findByUserId(userId);
    }

    // Get Single Goal - Verify ownership
    public Goal getGoal(Long id, String email) {
        Goal goal = goalRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found with id: " + id));

        Long userId = getUserIdByEmail(email);
        if (!goal.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Goal not found or access denied");
        }

        return goal;
    }

    // Update Goal - Verify ownership
    @Transactional
    public Goal updateGoal(Long id, GoalRequest req, String email) {
        Goal goal = getGoal(id, email);

        if (req.getTitle() != null) goal.setTitle(req.getTitle());
        if (req.getTargetDate() != null) goal.setTargetDate(req.getTargetDate());
        if (req.getPriority() != null) goal.setPriority(req.getPriority());
        if (req.getStatus() != null) goal.setStatus(req.getStatus());
        if (req.getMotivationReason() != null) goal.setMotivationReason(req.getMotivationReason());

        return goalRepo.save(goal);
    }

    // Delete Goal - Verify ownership
    @Transactional
    public void deleteGoal(Long id, String email) {
        Goal goal = getGoal(id, email);
        goalRepo.delete(goal);
    }

    // Log Daily Progress - DATE IS AUTO-GENERATED (TODAY)
    @Transactional
    public GoalProgress logProgress(GoalProgressRequest req, String email) {
        // Verify the goal belongs to this user
        getGoal(req.getGoalId(), email);

        // Auto-generate today's date
        LocalDate today = LocalDate.now();

        // Check if already logged TODAY
        Optional<GoalProgress> existing = progressRepo.findByGoalIdAndDate(
                req.getGoalId(), today
        );

        GoalProgress progress;
        if (existing.isPresent()) {
            // Update existing entry for TODAY
            progress = existing.get();
            progress.setTodayProgress(req.getTodayProgress());
            progress.setTotalProgress(req.getTotalProgress());
            progress.setNotes(req.getNotes());
            System.out.println("✅ Updated progress for date: " + today);
        } else {
            // Create new entry for TODAY
            progress = new GoalProgress();
            progress.setGoalId(req.getGoalId());
            progress.setDate(today); // ✅ AUTO-GENERATED
            progress.setTodayProgress(req.getTodayProgress());
            progress.setTotalProgress(req.getTotalProgress());
            progress.setNotes(req.getNotes());
            System.out.println("✅ Created new progress for date: " + today);
        }

        return progressRepo.save(progress);
    }

    // Get Progress History - Fixed with proper date handling
    public List<GoalProgress> getProgressHistory(Long goalId, Integer days, String email) {
        // Verify ownership
        getGoal(goalId, email);

        // Calculate date range
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(days != null ? days : 7);

        System.out.println("🔍 Fetching progress for goalId: " + goalId);
        System.out.println("📅 Date Range: " + startDate + " to " + today);

        // Fetch data using BETWEEN (inclusive on both ends)
        List<GoalProgress> results = progressRepo.findByGoalIdAndDateBetween(
                goalId, startDate, today
        );

        System.out.println("📊 Found " + results.size() + " progress entries");

        // If no results, debug by showing all dates
        if (results.isEmpty()) {
            System.out.println("⚠️ No progress found in date range");
            List<GoalProgress> allProgress = progressRepo.findByGoalId(goalId);
            if (!allProgress.isEmpty()) {
                System.out.println("📅 Available dates in DB:");
                allProgress.forEach(p -> System.out.println("   - " + p.getDate() + " (Progress: " + p.getTotalProgress() + "%)"));
            } else {
                System.out.println("❌ No progress data exists for this goal");
            }
        }

        return results;
    }

    // Get Today's Dashboard - Only for authenticated user
    public Map<String, Object> getDashboard(String email, LocalDate date) {
        Long userId = getUserIdByEmail(email);

        // If no date provided, use today
        LocalDate targetDate = (date != null) ? date : LocalDate.now();

        List<Goal> goals = goalRepo.findByUserIdAndStatus(userId, "in_progress");
        List<Map<String, Object>> goalData = new ArrayList<>();

        for (Goal goal : goals) {
            Map<String, Object> data = new HashMap<>();
            data.put("goal", goal);

            Optional<GoalProgress> todayProgress = progressRepo.findByGoalIdAndDate(
                    goal.getId(), targetDate
            );
            data.put("todayProgress", todayProgress.orElse(null));

            goalData.add(data);
        }

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("date", targetDate);
        dashboard.put("goals", goalData);
        return dashboard;
    }

    // Get Dashboard for TODAY (simplified version)
    public Map<String, Object> getTodayDashboard(String email) {
        return getDashboard(email, LocalDate.now());
    }

    // DEBUGGING: Get all progress for a goal
    public List<GoalProgress> getAllProgressForGoal(Long goalId, String email) {
        getGoal(goalId, email);
        List<GoalProgress> allProgress = progressRepo.findByGoalId(goalId);
        System.out.println("📊 Total progress entries: " + allProgress.size());
        return allProgress;
    }
}