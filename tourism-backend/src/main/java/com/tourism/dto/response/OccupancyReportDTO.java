package com.tourism.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OccupancyReportDTO {
    private Long scheduleId;
    private String packageName;
    private java.time.LocalDate startDate;
    private int capacity;
    private int booked;
    private double occupancyPercentage;
}
