package com.selfhelp.diary.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/diaries/{diaryId}/comments")
@RequiredArgsConstructor
public class DairyCommentController {

    private final DairyCommentService dairyCommentService;

    // 💬 ADD COMMENT
    @PostMapping
    public ResponseEntity<DairyCommentDTO> addComment(
            @PathVariable Long diaryId,
            @RequestBody CommentRequest request,
            @AuthenticationPrincipal String email
    ) {
        DairyCommentDTO comment = dairyCommentService.addComment(diaryId, request.getContent(), email);
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }

    // 📖 GET COMMENTS
    @GetMapping
    public ResponseEntity<Page<DairyCommentDTO>> getComments(
            @PathVariable Long diaryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Page<DairyCommentDTO> comments = dairyCommentService.getComments(diaryId, page, size);
        return ResponseEntity.ok(comments);
    }

    // 🔢 GET COMMENT COUNT
    @GetMapping("/count")
    public ResponseEntity<Long> getCommentCount(@PathVariable Long diaryId) {
        long count = dairyCommentService.getCommentCount(diaryId);
        return ResponseEntity.ok(count);
    }

    // 🗑️ DELETE COMMENT
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long diaryId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal String email
    ) {
        dairyCommentService.deleteComment(commentId, email);
        return ResponseEntity.noContent().build();
    }
}