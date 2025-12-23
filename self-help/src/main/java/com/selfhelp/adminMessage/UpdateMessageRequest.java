package com.selfhelp.adminMessage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateMessageRequest {
    @NotBlank(message = "Content cannot be empty")
    @Size(max = 1000, message = "Content must be less than 1000 characters")
    private String content;
    @NotBlank(message = "Title is required")
    private String title;
}
