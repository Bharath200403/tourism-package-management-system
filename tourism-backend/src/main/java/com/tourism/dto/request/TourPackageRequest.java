package com.tourism.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class TourPackageRequest {

    @NotBlank(message = "Package code is required")
    private String packageCode;

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Destination is required")
    private Long destinationId;

    private String description;

    @Positive(message = "Duration must be positive")
    private int durationDays;

    @NotNull(message = "Base price is required")
    @Positive(message = "Base price must be positive")
    private BigDecimal basePrice;

    private String travelType;
    private String packageType;
    private String status;

    private List<String> inclusions;
    private List<String> exclusions;
}
