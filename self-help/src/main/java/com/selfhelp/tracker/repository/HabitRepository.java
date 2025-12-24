package com.selfhelp.tracker.repository;

import com.selfhelp.tracker.model.Habit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {

    // Find all habits by user ID
    List<Habit> findByUserId(Long userId);

    // Find habits by user ID ordered by title
    List<Habit> findByUserIdOrderByTitleAsc(Long userId);

    // Find habits by user ID ordered by creation date
    List<Habit> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Count habits for a user
    long countByUserId(Long userId);
}
