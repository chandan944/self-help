package com.selfhelp.diary.comment;

import com.selfhelp.diary.Diary;
import com.selfhelp.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "diary_comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiaryComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)  // Add this line
    private Diary diary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @CreationTimestamp
    private LocalDateTime createdAt;
}