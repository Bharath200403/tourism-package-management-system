package com.tourism.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequest {

    @NotNull(message = "Booking is required")
    private Long bookingId;

    @NotBlank(message = "Payment mode is required")
    private String paymentMode;
}
