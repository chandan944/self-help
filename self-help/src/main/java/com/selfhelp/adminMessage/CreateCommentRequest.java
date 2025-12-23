package com.selfhelp.adminMessage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateCommentRequest {
    @NotBlank(message = "Comment cannot be empty")
    @Size(max = 500, message = "Comment must be less than 500 characters")
    private String content;
}
