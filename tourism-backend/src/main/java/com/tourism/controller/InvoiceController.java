package com.tourism.controller;

import com.tourism.dto.response.InvoiceResponseDTO;
import com.tourism.security.AppUserDetails;
import com.tourism.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponseDTO> getById(@PathVariable Long id, @AuthenticationPrincipal AppUserDetails principal) {
        return ResponseEntity.ok(invoiceService.getById(id, principal.getUser()));
    }

    @GetMapping("/by-booking/{bookingId}")
    public ResponseEntity<InvoiceResponseDTO> getByBooking(@PathVariable Long bookingId, @AuthenticationPrincipal AppUserDetails principal) {
        return ResponseEntity.ok(invoiceService.getByBooking(bookingId, principal.getUser()));
    }
}
