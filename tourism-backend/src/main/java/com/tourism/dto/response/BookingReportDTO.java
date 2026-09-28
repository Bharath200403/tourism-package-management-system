package com.tourism.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookingReportDTO {
    private long totalBookings;
    private long pending;
    private long confirmed;
    private long completed;
    private long cancelled;
}
