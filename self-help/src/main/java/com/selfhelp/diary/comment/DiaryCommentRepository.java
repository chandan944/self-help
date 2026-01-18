package com.selfhelp.diary.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DiaryCommentRepository extends JpaRepository<DiaryComment, Long> {

    @Query("""
   SELECT c FROM DiaryComment c
   WHERE c.diary.id = :diaryId
   ORDER BY c.createdAt DESC
""")
    Page<DiaryComment> findByDiaryId(Long diaryId, Pageable pageable);

    long countByDiaryId(Long diaryId);
}