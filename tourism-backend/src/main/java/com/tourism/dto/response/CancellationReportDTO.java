package com.tourism.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class CancellationReportDTO {
    private long totalCancellations;
    private BigDecimal totalRefundAmount;
}
