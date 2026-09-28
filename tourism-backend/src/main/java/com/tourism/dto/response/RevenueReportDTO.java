package com.tourism.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class RevenueReportDTO {
    private BigDecimal totalRevenue;
    private BigDecimal refundedAmount;
    private BigDecimal netRevenue;
}
