package com.tourism.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class PackageScheduleResponseDTO {
    private Long id;
    private Long packageId;
    private LocalDate startDate;
    private LocalDate endDate;
    private int capacity;
    private int availableSeats;
    private String status;
}
