package com.selfhelp.diary.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiaryCommentDTO {
    private Long id;
    private String content;
    private String authorName;
    private Long authorId;
    private LocalDateTime createdAt;
}