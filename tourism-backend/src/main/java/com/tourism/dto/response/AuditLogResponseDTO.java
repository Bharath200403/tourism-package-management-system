package com.tourism.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AuditLogResponseDTO {
    private Long id;
    private String actorUsername;
    private String action;
    private String entityType;
    private String entityId;
    private String details;
    private LocalDateTime createdAt;
}
