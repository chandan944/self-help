package com.selfhelp.tracker.repository;

import com.selfhelp.tracker.model.GoalProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface GoalProgressRepository extends JpaRepository<GoalProgress, Long> {
    List<GoalProgress> findByGoalId(Long goalId);
    Optional<GoalProgress> findByGoalIdAndDate(Long goalId, LocalDate date);
    List<GoalProgress> findByGoalIdAndDateBetween(Long goalId, LocalDate start, LocalDate end);
}
