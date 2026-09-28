package com.tourism.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItineraryRequest {

    @Positive(message = "Day number must be positive")
    private int dayNumber;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;
    private String activities;
    private int displayOrder;
}
