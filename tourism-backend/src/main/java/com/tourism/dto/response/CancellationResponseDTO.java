package com.tourism.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class CancellationResponseDTO {
    private Long id;
    private Long bookingId;
    private String cancellationReason;
    private String refundStatus;
    private BigDecimal refundAmount;
    private LocalDateTime cancelledAt;
}
