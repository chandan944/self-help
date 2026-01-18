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
public class DiaryCommentService {

    private final DiaryCommentRepository commentRepository;
    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;

    // 💬 ADD COMMENT
    @Transactional
    public DiaryCommentDTO addComment(Long diaryId, String content, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("Diary not found"));

        DiaryComment diaryComment = DiaryComment.builder()
                .content(content)
                .diary(diary)
                .author(user)
                .build();

        DiaryComment savedDiaryComment = commentRepository.save(diaryComment);

        return DiaryCommentDTO.builder()
                .id(savedDiaryComment.getId())
                .content(savedDiaryComment.getContent())
                .authorName(savedDiaryComment.getAuthor().getName())
                .authorId(savedDiaryComment.getAuthor().getId())
                .createdAt(savedDiaryComment.getCreatedAt())
                .build();
    }

    // 📖 GET COMMENTS FOR DIARY
    public Page<DiaryCommentDTO> getComments(Long diaryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return commentRepository.findByDiaryId(diaryId, pageable)
                .map(dairyComment -> DiaryCommentDTO.builder()
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
        DiaryComment diaryComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!diaryComment.getAuthor().getEmail().equals(email)) {
            throw new RuntimeException("Not authorized to delete this comment");
        }

        commentRepository.delete(diaryComment);
    }
}