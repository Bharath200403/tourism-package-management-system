package com.tourism.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class BookingResponseDTO {
    private Long id;
    private String bookingReference;
    private Long customerId;
    private String customerName;
    private Long scheduleId;
    private Long packageId;
    private String packageName;
    private java.time.LocalDate scheduleStartDate;
    private java.time.LocalDate scheduleEndDate;
    private int travelerCount;
    private BigDecimal totalAmount;
    private String bookingStatus;
    private LocalDateTime bookingDate;
    private List<TravelerResponseDTO> travelers;
    private PaymentResponseDTO payment;
}
