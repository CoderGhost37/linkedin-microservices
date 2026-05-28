package com.kushagramathur.linkedIn.post_service.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PostCreateRequestDto {
    private String content;
    private MultipartFile file;
}
