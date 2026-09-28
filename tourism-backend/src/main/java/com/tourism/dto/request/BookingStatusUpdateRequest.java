package com.tourism.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingStatusUpdateRequest {

    @NotBlank(message = "Status is required")
    private String status;
}
