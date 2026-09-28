package com.tourism.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DestinationRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String state;
    private String country;
    private String description;
    private String bestSeason;
    private String estimatedDuration;
    private String status;
}
