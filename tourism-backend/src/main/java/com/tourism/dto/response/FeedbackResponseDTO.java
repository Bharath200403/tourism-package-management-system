package com.tourism.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class FeedbackResponseDTO {
    private Long id;
    private String customerName;
    private String subject;
    private String message;
    private LocalDateTime createdAt;
}
