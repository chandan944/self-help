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
public class DairyCommentService {

    private final DiaryCommentRepository commentRepository;
    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;

    // 💬 ADD COMMENT
    @Transactional
    public DairyCommentDTO addComment(Long diaryId, String content, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("Diary not found"));

        DairyComment dairyComment = DairyComment.builder()
                .content(content)
                .diary(diary)
                .author(user)
                .build();

        DairyComment savedDairyComment = commentRepository.save(dairyComment);

        return DairyCommentDTO.builder()
                .id(savedDairyComment.getId())
                .content(savedDairyComment.getContent())
                .authorName(savedDairyComment.getAuthor().getName())
                .authorId(savedDairyComment.getAuthor().getId())
                .createdAt(savedDairyComment.getCreatedAt())
                .build();
    }

    // 📖 GET COMMENTS FOR DIARY
    public Page<DairyCommentDTO> getComments(Long diaryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return commentRepository.findByDiaryId(diaryId, pageable)
                .map(dairyComment -> DairyCommentDTO.builder()
                        .id(dairyComment.getId())
                        .content(dairyComment.getContent())
                        .authorName(dairyComment.getAuthor().getName())
                        .authorId(dairyComment.getAuthor().getId())
                        .createdAt(dairyComment.getCreatedAt())
                        .build());
    }

    // 🔢 GET COMMENT COUNT
    public long getCommentCount(Long diaryId) {
        return commentRepository.countByDiaryId(diaryId);
    }

    // 🗑️ DELETE COMMENT
    @Transactional
    public void deleteComment(Long commentId, String email) {
        DairyComment dairyComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!dairyComment.getAuthor().getEmail().equals(email)) {
            throw new RuntimeException("Not authorized to delete this comment");
        }

        commentRepository.delete(dairyComment);
    }
}