package com.selfhelp.diary.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.diary.id = :diaryId ORDER BY c.createdAt DESC")
    Page<Comment> findByDiaryId(Long diaryId, Pageable pageable);

    long countByDiaryId(Long diaryId);
}