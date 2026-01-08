package com.selfhelp.adminMessage.controller;

import com.selfhelp.adminMessage.CreateCommentRequest;
import com.selfhelp.adminMessage.CreateMessageRequest;
import com.selfhelp.adminMessage.UpdateMessageRequest;
import com.selfhelp.adminMessage.dto.CommentDto;
import com.selfhelp.adminMessage.dto.MessageDto;
import com.selfhelp.adminMessage.service.MessageService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/messages")  // ✅ FIXED: Changed from /api to /api/messages
public class MessageController {
    private final MessageService messageService;

    // ============================================
    // PUBLIC ENDPOINTS - Everyone can view
    // ============================================

    @GetMapping
    public ResponseEntity<Page<MessageDto>> getAllMessages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(messageService.getAllMessages(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageDto> getMessage(@PathVariable Long id) {
        return ResponseEntity.ok(messageService.getMessage(id));
    }

    // ============================================
    // PUBLIC ENDPOINT - Anyone logged in can comment
    // ============================================

    @PostMapping("/{messageId}/comments")
    public ResponseEntity<CommentDto> addComment(
            @PathVariable Long messageId,
            @Valid @RequestBody CreateCommentRequest request,
            @AuthenticationPrincipal String email) {
        CommentDto comment = messageService.addComment(messageId, request, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }

    // ============================================
    // ADMIN ONLY ENDPOINTS - Create, Update, Delete Messages
    // ============================================

    @PostMapping("/admin")
    public ResponseEntity<MessageDto> createMessage(
            @Valid @RequestBody CreateMessageRequest request,
            @AuthenticationPrincipal String email) {
        MessageDto createdMessage = messageService.createMessage(request, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMessage);
    }

    @PutMapping("/admin/{id}")
    public ResponseEntity<MessageDto> updateMessage(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMessageRequest request,
            @AuthenticationPrincipal String email) {
        MessageDto updatedMessage = messageService.updateMessage(id, request, email);
        return ResponseEntity.ok(updatedMessage);
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable Long id,
            @AuthenticationPrincipal String email) {
        messageService.deleteMessage(id, email);
        return ResponseEntity.noContent().build();
    }

    // ============================================
    // ADMIN ONLY - Delete any user's comment
    // ============================================

    @DeleteMapping("/admin/{messageId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long messageId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal String email) {
        messageService.deleteComment(messageId, commentId, email);
        return ResponseEntity.noContent().build();
    }
}