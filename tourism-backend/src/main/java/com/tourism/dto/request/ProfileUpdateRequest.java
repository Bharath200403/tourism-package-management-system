package com.tourism.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProfileUpdateRequest {
    private String fullName;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String country;
    private String preferredTravelType;
    private BigDecimal preferredBudget;
}
