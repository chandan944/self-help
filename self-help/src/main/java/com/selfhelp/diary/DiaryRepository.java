package com.selfhelp.diary;

import com.selfhelp.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface DiaryRepository extends JpaRepository<Diary, Long> {

    Page<Diary> findByAuthor(User author, Pageable pageable);

    @Query("SELECT d FROM Diary d WHERE d.visibility = 'PUBLIC'")
    Page<Diary> findPublicDiaries(Pageable pageable);

    @Query("SELECT d FROM Diary d JOIN FETCH d.author WHERE d.id = :id")
    Optional<Diary> findByIdWithAuthor(@Param("id") Long id);

    // 🆕 NEW METHOD: Find diary by author and date
    Optional<Diary> findByAuthorAndEntryDate(User author, LocalDate entryDate);
}