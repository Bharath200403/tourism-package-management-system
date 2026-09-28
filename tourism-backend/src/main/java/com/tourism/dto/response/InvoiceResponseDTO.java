package com.tourism.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class InvoiceResponseDTO {
    private Long id;
    private String invoiceNumber;
    private String bookingReference;
    private String customerName;
    private String packageName;
    private java.time.LocalDate scheduleStartDate;
    private int travelerCount;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal charges;
    private BigDecimal total;
    private String paymentStatus;
    private LocalDateTime invoiceDate;
}
