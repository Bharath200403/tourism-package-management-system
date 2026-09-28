package com.tourism.controller;

import com.tourism.dto.request.PaymentRequest;
import com.tourism.dto.response.BookingResponseDTO;
import com.tourism.security.AppUserDetails;
import com.tourism.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/simulate")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BookingResponseDTO> simulate(@Valid @RequestBody PaymentRequest request,
                                                        @AuthenticationPrincipal AppUserDetails principal) {
        return ResponseEntity.ok(paymentService.simulatePayment(principal.getUser(), request));
    }
}
