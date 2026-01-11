package com.selfhelp.todo;


import com.selfhelp.todo.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

    // Find all todos for a user, ordered by creation date
    Page<Todo> findByUserEmailOrderByCreatedAtDesc(String userEmail, Pageable pageable);

    // Find todos by completion status for a user
    Page<Todo> findByUserEmailAndCompletedOrderByCreatedAtDesc(
            String userEmail, Boolean completed, Pageable pageable);

    // Find all todos (completed or not) for a user
    List<Todo> findByUserEmailAndCompleted(String userEmail, Boolean completed);

    // Find todos with due dates in a specific range
    @Query("SELECT t FROM Todo t WHERE t.userEmail = :email " +
            "AND t.dueDate BETWEEN :start AND :end ORDER BY t.dueDate ASC")
    List<Todo> findByUserEmailAndDueDateBetween(
            @Param("email") String email,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    // Count completed todos
    @Query("SELECT COUNT(t) FROM Todo t WHERE t.userEmail = :email AND t.completed = true")
    Long countCompletedByUserEmail(@Param("email") String email);

    // Count pending todos
    @Query("SELECT COUNT(t) FROM Todo t WHERE t.userEmail = :email AND t.completed = false")
    Long countPendingByUserEmail(@Param("email") String email);

    // Count todos completed after a specific date
    @Query("SELECT COUNT(t) FROM Todo t WHERE t.userEmail = :email " +
            "AND t.completed = true AND t.completedAt >= :date")
    Long countCompletedSince(@Param("email") String email, @Param("date") LocalDateTime date);
}