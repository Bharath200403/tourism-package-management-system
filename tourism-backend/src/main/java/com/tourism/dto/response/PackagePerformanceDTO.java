package com.tourism.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class PackagePerformanceDTO {
    private Long packageId;
    private String packageName;
    private long totalBookings;
    private BigDecimal revenue;
    private double occupancyPercentage;
}
