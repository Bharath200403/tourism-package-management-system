package com.tourism.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BookingRequest {

    @NotNull(message = "Schedule is required")
    private Long scheduleId;

    @NotEmpty(message = "At least one traveler is required")
    @Valid
    private List<TravelerRequest> travelers;
}
