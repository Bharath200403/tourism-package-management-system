package com.tourism.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CustomerDashboardDTO {
    private long upcomingTrips;
    private long activeBookings;
    private long completedTrips;
    private long cancelledBookings;
    private List<BookingResponseDTO> recentBookings;
    private List<TourPackageResponseDTO> recommendedPackages;
}
