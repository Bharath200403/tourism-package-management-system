package com.tourism.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class AdminDashboardDTO {
    private long totalCustomers;
    private long totalOperators;
    private long totalPackages;
    private long activePackages;
    private long upcomingTours;
    private long totalBookings;
    private long pendingBookings;
    private long confirmedBookings;
    private long cancelledBookings;
    private long completedBookings;
    private BigDecimal revenue;
    private double occupancyPercentage;
    private List<PopularPackageDTO> popularPackages;
}
