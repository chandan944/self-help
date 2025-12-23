package com.selfhelp.tracker.service;

import com.selfhelp.tracker.dto.HabitLogRequest;
import com.selfhelp.tracker.dto.HabitRequest;
import com.selfhelp.tracker.exception.ResourceNotFoundException;
import com.selfhelp.tracker.model.Habit;
import com.selfhelp.tracker.model.HabitLog;
import com.selfhelp.tracker.repository.HabitLogRepository;
import com.selfhelp.tracker.repository.HabitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class HabitService {

    private final HabitRepository habitRepo;
    private final HabitLogRepository logRepo;

    // Create Habit
    @Transactional
    public Habit createHabit(HabitRequest req) {
        Habit habit = new Habit();
        habit.setUserId(req.getUserId());
        habit.setTitle(req.getTitle());
        habit.setTargetValue(req.getTargetValue());
        habit.setBestStreak(0);
        return habitRepo.save(habit);
    }

    // Get All Habits for User
    public List<Habit> getUserHabits(Long userId) {
        return habitRepo.findByUserId(userId);
    }

    // Get Single Habit
    public Habit getHabit(Long id) {
        return habitRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Habit not found with id: " + id));
    }

    // Update Habit
    @Transactional
    public Habit updateHabit(Long id, HabitRequest req) {
        Habit habit = getHabit(id);
        if (req.getTitle() != null) habit.setTitle(req.getTitle());
        if (req.getTargetValue() != null) habit.setTargetValue(req.getTargetValue());
        return habitRepo.save(habit);
    }

    // Delete Habit
    @Transactional
    public void deleteHabit(Long id) {
        Habit habit = getHabit(id);
        habitRepo.delete(habit);
    }

    // Log Daily Habit
    @Transactional
    public HabitLog logHabit(HabitLogRequest req) {
        Habit habit = getHabit(req.getHabitId());

        // Check if already logged today
        Optional<HabitLog> existing = logRepo.findByHabitIdAndDate(
                req.getHabitId(), req.getDate()
        );

        HabitLog log;
        if (existing.isPresent()) {
            log = existing.get();
            log.setStatus(req.getStatus());
            log.setCurrentStreak(req.getCurrentStreak());
            log.setMoodAfter(req.getMoodAfter());
            log.setNotes(req.getNotes());
        } else {
            log = new HabitLog();
            log.setHabitId(req.getHabitId());
            log.setDate(req.getDate());
            log.setStatus(req.getStatus());
            log.setCurrentStreak(req.getCurrentStreak());
            log.setMoodAfter(req.getMoodAfter());
            log.setNotes(req.getNotes());
        }

        // Update best streak
        if (req.getCurrentStreak() > habit.getBestStreak()) {
            habit.setBestStreak(req.getCurrentStreak());
            habitRepo.save(habit);
        }

        return logRepo.save(log);
    }

    // Get Habit History
    public List<HabitLog> getHabitHistory(Long habitId, Integer days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days != null ? days : 7);
        return logRepo.findByHabitIdAndDateBetween(habitId, startDate, endDate);
    }

    // Get Today's Habits
    public Map<String, Object> getTodayHabits(Long userId, LocalDate date) {
        List<Habit> habits = habitRepo.findByUserId(userId);
        List<Map<String, Object>> habitData = new ArrayList<>();

        for (Habit habit : habits) {
            Map<String, Object> data = new HashMap<>();
            data.put("habit", habit);

            Optional<HabitLog> todayLog = logRepo.findByHabitIdAndDate(habit.getId(), date);
            data.put("todayLog", todayLog.orElse(null));

            habitData.add(data);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("date", date);
        response.put("habits", habitData);
        return response;
    }
}
