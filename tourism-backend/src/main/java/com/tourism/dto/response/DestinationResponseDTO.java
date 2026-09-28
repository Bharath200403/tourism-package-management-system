package com.tourism.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DestinationResponseDTO {
    private Long id;
    private String name;
    private String state;
    private String country;
    private String description;
    private String bestSeason;
    private String estimatedDuration;
    private String status;
}
