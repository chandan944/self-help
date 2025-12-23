package com.selfhelp.adminMessage.service;

import com.selfhelp.adminMessage.CreateCommentRequest;
import com.selfhelp.adminMessage.CreateMessageRequest;
import com.selfhelp.adminMessage.UpdateMessageRequest;
import com.selfhelp.adminMessage.dto.CommentDto;
import com.selfhelp.adminMessage.dto.MessageDto;
import com.selfhelp.adminMessage.message.Comment;
import com.selfhelp.adminMessage.message.Message;
import com.selfhelp.adminMessage.repository.CommentRepository;
import com.selfhelp.adminMessage.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public Page<MessageDto> getAllMessages(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return messageRepository.findAll(pageable).map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public MessageDto getMessage(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found with id: " + id));
        return convertToDto(message);
    }

    @Transactional
    public MessageDto createMessage(CreateMessageRequest request, String email) {
        Message message = new Message();
        message.setTitle(request.getTitle());
        message.setContent(request.getContent());
        message.setAuthorEmail(email);

        Message savedMessage = messageRepository.save(message);
        return convertToDto(savedMessage);
    }

    @Transactional
    public MessageDto updateMessage(Long id, UpdateMessageRequest request, String email) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found with id: " + id));

        if (!message.getAuthorEmail().equals(email)) {
            throw new RuntimeException("You can only edit your own messages");
        }

        message.setTitle(request.getTitle());
        message.setContent(request.getContent());
        Message updatedMessage = messageRepository.save(message);
        return convertToDto(updatedMessage);
    }

    @Transactional
    public void deleteMessage(Long id, String email) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found with id: " + id));

        if (!message.getAuthorEmail().equals(email)) {
            throw new RuntimeException("You can only delete your own messages");
        }

        messageRepository.delete(message);
    }

    @Transactional
    public CommentDto addComment(Long messageId, CreateCommentRequest request, String email) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found with id: " + messageId));

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setAuthorEmail(email);
        comment.setMessage(message);

        Comment savedComment = commentRepository.save(comment);
        return convertCommentToDto(savedComment);
    }

    private MessageDto convertToDto(Message message) {
        MessageDto dto = new MessageDto();
        dto.setId(message.getId());
        dto.setTitle(message.getTitle());
        dto.setContent(message.getContent());
        dto.setAuthorEmail(message.getAuthorEmail());
        dto.setCreatedAt(message.getCreatedAt());
        dto.setUpdatedAt(message.getUpdatedAt());
        dto.setComments(message.getComments().stream()
                .map(this::convertCommentToDto)
                .collect(Collectors.toList()));
        dto.setCommentCount(message.getComments().size());
        return dto;
    }

    private CommentDto convertCommentToDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setAuthorEmail(comment.getAuthorEmail());
        dto.setCreatedAt(comment.getCreatedAt());
        return dto;
    }

    @Transactional
    public void deleteComment(Long messageId, Long commentId, String email) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found with id: " + messageId));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));

        // Verify the comment belongs to the message
        if (!comment.getMessage().getId().equals(messageId)) {
            throw new RuntimeException("Comment does not belong to this message");
        }

        // Admin can delete any comment (no author check needed)
        commentRepository.delete(comment);
    }
}
