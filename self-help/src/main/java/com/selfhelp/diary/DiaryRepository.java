package com.selfhelp.diary;

import com.selfhelp.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {







    @Query("SELECT d FROM Diary d JOIN FETCH d.author WHERE d.id = :id")
    Optional<Diary> findByIdWithAuthor(@Param("id") Long id);

    Page<Diary> findByAuthor(User author, Pageable pageable);

    @Query("""
   SELECT d FROM Diary d
   JOIN FETCH d.author
   WHERE d.visibility = 'PUBLIC'
""")
    Page<Diary> findPublicDiaries(Pageable pageable);

}
