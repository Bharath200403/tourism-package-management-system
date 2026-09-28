package com.tourism.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TravelerResponseDTO {
    private Long id;
    private String fullName;
    private int age;
    private String gender;
    private String contact;
    private String specialRequirement;
}
