package com.kushagramathur.linkedIn.post_service.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PostDto {
    private Long Id;
    private String content;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
