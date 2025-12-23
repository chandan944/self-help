package com.selfhelp.adminMessage.repository;


import com.selfhelp.adminMessage.message.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("SELECT m FROM Message m LEFT JOIN FETCH m.comments ORDER BY m.createdAt DESC")
    Page<Message> findAllWithComments(Pageable pageable);
}