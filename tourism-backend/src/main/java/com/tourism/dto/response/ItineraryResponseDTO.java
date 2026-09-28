package com.tourism.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ItineraryResponseDTO {
    private Long id;
    private int dayNumber;
    private String title;
    private String description;
    private String activities;
    private int displayOrder;
}
