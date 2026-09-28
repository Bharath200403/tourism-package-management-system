package com.tourism.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationResponseDTO {
    private Long id;
    private String title;
    private String message;
    private boolean read;
    private LocalDateTime createdAt;
}
