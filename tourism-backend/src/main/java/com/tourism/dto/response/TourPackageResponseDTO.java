package com.tourism.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class TourPackageResponseDTO {
    private Long id;
    private String packageCode;
    private String name;
    private Long destinationId;
    private String destinationName;
    private String description;
    private int durationDays;
    private BigDecimal basePrice;
    private String travelType;
    private String packageType;
    private String status;
    private List<String> inclusions;
    private List<String> exclusions;
    private List<ItineraryResponseDTO> itinerary;
    private List<PackageScheduleResponseDTO> schedules;
    private Double averageRating;
    private Long reviewCount;
}
