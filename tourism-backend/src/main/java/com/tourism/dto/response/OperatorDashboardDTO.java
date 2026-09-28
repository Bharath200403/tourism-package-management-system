package com.tourism.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class OperatorDashboardDTO {
    private long totalPackages;
    private long activePackages;
    private long totalBookings;
    private long pendingBookings;
    private List<TourPackageResponseDTO> myPackages;
}
