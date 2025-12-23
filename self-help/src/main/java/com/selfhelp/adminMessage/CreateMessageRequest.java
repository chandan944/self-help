package com.selfhelp.adminMessage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;

@Data
public class CreateMessageRequest {
    @NotBlank(message = "Content cannot be empty")
    @Size(max = 1000, message = "Content must be less than 1000 characters")
    private String content;

    @Getter
    @NotBlank(message = "Title is required")
    private String title;

}
