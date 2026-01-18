package com.selfhelp.diary.comment;

import com.selfhelp.diary.Diary;
import com.selfhelp.diary.DiaryRepository;
import com.selfhelp.user.User;
import com.selfhelp.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;

    // 💬 ADD COMMENT
    @Transactional
    public CommentDTO addComment(Long diaryId, String content, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("Diary not found"));

        Comment comment = Comment.builder()
                .content(content)
                .diary(diary)
                .author(user)
                .build();

        Comment savedComment = commentRepository.save(comment);

        return CommentDTO.builder()
                .id(savedComment.getId())
                .content(savedComment.getContent())
                .authorName(savedComment.getAuthor().getName())
                .authorId(savedComment.getAuthor().getId())
                .createdAt(savedComment.getCreatedAt())
                .build();
    }

    // 📖 GET COMMENTS FOR DIARY
    public Page<CommentDTO> getComments(Long diaryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return commentRepository.findByDiaryId(diaryId, pageable)
                .map(comment -> CommentDTO.builder()
                        .id(comment.getId())
                        .content(comment.getContent())
                        .authorName(comment.getAuthor().getName())
                        .authorId(comment.getAuthor().getId())
                        .createdAt(comment.getCreatedAt())
                        .build());
    }

    // 🔢 GET COMMENT COUNT
    public long getCommentCount(Long diaryId) {
        return commentRepository.countByDiaryId(diaryId);
    }

    // 🗑️ DELETE COMMENT
    @Transactional
    public void deleteComment(Long commentId, String email) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getAuthor().getEmail().equals(email)) {
            throw new RuntimeException("Not authorized to delete this comment");
        }

        commentRepository.delete(comment);
    }
}