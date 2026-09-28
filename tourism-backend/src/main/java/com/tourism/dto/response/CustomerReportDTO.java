package com.tourism.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class CustomerReportDTO {
    private Long customerId;
    private String customerName;
    private long totalBookings;
    private BigDecimal totalSpend;
}
