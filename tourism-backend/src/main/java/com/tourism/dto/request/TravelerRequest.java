package com.tourism.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TravelerRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @Positive(message = "Age must be positive")
    private int age;

    private String gender;
    private String contact;
    private String specialRequirement;
}
