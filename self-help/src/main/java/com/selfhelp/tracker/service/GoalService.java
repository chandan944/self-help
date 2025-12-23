package com.selfhelp.tracker.service;
import com.selfhelp.tracker.dto.GoalProgressRequest;
import com.selfhelp.tracker.dto.GoalRequest;
import com.selfhelp.tracker.exception.ResourceNotFoundException;
import com.selfhelp.tracker.model.Goal;
import com.selfhelp.tracker.model.GoalProgress;
import com.selfhelp.tracker.repository.GoalProgressRepository;
import com.selfhelp.tracker.repository.GoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepo;
    private final GoalProgressRepository progressRepo;

    // Create Goal
    @Transactional
    public Goal createGoal(GoalRequest req) {
        Goal goal = new Goal();
        goal.setUserId(req.getUserId());
        goal.setTitle(req.getTitle());
        goal.setStartDate(req.getStartDate());
        goal.setTargetDate(req.getTargetDate());
        goal.setPriority(req.getPriority());
        goal.setStatus(req.getStatus() != null ? req.getStatus() : "in_progress");
        goal.setMotivationReason(req.getMotivationReason());
        return goalRepo.save(goal);
    }

    // Get All Goals for User
    public List<Goal> getUserGoals(Long userId) {
        return goalRepo.findByUserId(userId);
    }

    // Get Single Goal
    public Goal getGoal(Long id) {
        return goalRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found with id: " + id));
    }

    // Update Goal
    @Transactional
    public Goal updateGoal(Long id, GoalRequest req) {
        Goal goal = getGoal(id);
        if (req.getTitle() != null) goal.setTitle(req.getTitle());
        if (req.getTargetDate() != null) goal.setTargetDate(req.getTargetDate());
        if (req.getPriority() != null) goal.setPriority(req.getPriority());
        if (req.getStatus() != null) goal.setStatus(req.getStatus());
        if (req.getMotivationReason() != null) goal.setMotivationReason(req.getMotivationReason());
        return goalRepo.save(goal);
    }

    // Delete Goal
    @Transactional
    public void deleteGoal(Long id) {
        Goal goal = getGoal(id);
        goalRepo.delete(goal);
    }

    // Log Daily Progress
    @Transactional
    public GoalProgress logProgress(GoalProgressRequest req) {
        // Check if already logged today
        Optional<GoalProgress> existing = progressRepo.findByGoalIdAndDate(
                req.getGoalId(), req.getDate()
        );

        GoalProgress progress;
        if (existing.isPresent()) {
            // Update existing
            progress = existing.get();
            progress.setTodayProgress(req.getTodayProgress());
            progress.setTotalProgress(req.getTotalProgress());
            progress.setNotes(req.getNotes());
        } else {
            // Create new
            progress = new GoalProgress();
            progress.setGoalId(req.getGoalId());
            progress.setDate(req.getDate());
            progress.setTodayProgress(req.getTodayProgress());
            progress.setTotalProgress(req.getTotalProgress());
            progress.setNotes(req.getNotes());
        }
        return progressRepo.save(progress);
    }

    // Get Progress History
    public List<GoalProgress> getProgressHistory(Long goalId, Integer days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days != null ? days : 7);
        return progressRepo.findByGoalIdAndDateBetween(goalId, startDate, endDate);
    }

    // Get Today's Dashboard
    public Map<String, Object> getDashboard(Long userId, LocalDate date) {
        List<Goal> goals = goalRepo.findByUserIdAndStatus(userId, "in_progress");
        List<Map<String, Object>> goalData = new ArrayList<>();

        for (Goal goal : goals) {
            Map<String, Object> data = new HashMap<>();
            data.put("goal", goal);

            Optional<GoalProgress> todayProgress = progressRepo.findByGoalIdAndDate(goal.getId(), date);
            data.put("todayProgress", todayProgress.orElse(null));

            goalData.add(data);
        }

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("date", date);
        dashboard.put("goals", goalData);
        return dashboard;
    }
}
