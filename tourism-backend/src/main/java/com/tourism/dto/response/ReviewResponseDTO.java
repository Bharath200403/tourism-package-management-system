package com.tourism.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReviewResponseDTO {
    private Long id;
    private Long packageId;
    private String customerName;
    private int rating;
    private String reviewText;
    private String status;
    private LocalDateTime createdAt;
}
