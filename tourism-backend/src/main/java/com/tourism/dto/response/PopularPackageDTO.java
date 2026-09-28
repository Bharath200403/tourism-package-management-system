package com.tourism.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PopularPackageDTO {
    private Long packageId;
    private String packageName;
    private long bookingCount;
}
