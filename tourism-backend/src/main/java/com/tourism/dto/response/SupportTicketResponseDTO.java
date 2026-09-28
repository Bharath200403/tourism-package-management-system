package com.tourism.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SupportTicketResponseDTO {
    private Long id;
    private String customerName;
    private String subject;
    private String description;
    private String status;
    private String resolutionNotes;
    private LocalDateTime createdAt;
}
