package com.selfhelp.tracker.repository;

import com.selfhelp.tracker.model.GoalProgress;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface GoalProgressRepository extends JpaRepository<GoalProgress, Long> {

    // Find progress by goal ID and date
    Optional<GoalProgress> findByGoalIdAndDate(Long goalId, LocalDate date);

    // Find all progress entries for a goal
    List<GoalProgress> findByGoalId(Long goalId);

    // Find progress between dates
    List<GoalProgress> findByGoalIdAndDateBetween(Long goalId, LocalDate startDate, LocalDate endDate);

    // Find progress ordered by date descending (most recent first)
    List<GoalProgress> findByGoalIdOrderByDateDesc(Long goalId);

    // Delete all progress for a goal (useful for cascade delete)
    void deleteByGoalId(Long goalId);
}
