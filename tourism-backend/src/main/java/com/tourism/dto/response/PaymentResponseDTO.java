package com.tourism.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentResponseDTO {
    private Long id;
    private Long bookingId;
    private String transactionReference;
    private String paymentMode;
    private String paymentStatus;
    private BigDecimal amount;
    private LocalDateTime paidAt;
    private String note;
}
