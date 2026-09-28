package com.tourism.controller;

import com.tourism.dto.request.BookingRequest;
import com.tourism.dto.request.BookingStatusUpdateRequest;
import com.tourism.dto.request.CancellationRequest;
import com.tourism.dto.response.BookingResponseDTO;
import com.tourism.dto.response.CancellationResponseDTO;
import com.tourism.dto.response.PageResponseDTO;
import com.tourism.security.AppUserDetails;
import com.tourism.service.BookingService;
import com.tourism.service.CancellationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final CancellationService cancellationService;

    @PostMapping("/api/bookings")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BookingResponseDTO> create(@Valid @RequestBody BookingRequest request,
                                                      @AuthenticationPrincipal AppUserDetails principal) {
        return ResponseEntity.ok(bookingService.createBooking(principal.getUser(), request));
    }

    @GetMapping("/api/bookings/{id}")
    public ResponseEntity<BookingResponseDTO> getById(@PathVariable Long id, @AuthenticationPrincipal AppUserDetails principal) {
        return ResponseEntity.ok(bookingService.getById(id, principal.getUser()));
    }

    @GetMapping("/api/bookings/reference/{reference}")
    public ResponseEntity<BookingResponseDTO> getByReference(@PathVariable String reference, @AuthenticationPrincipal AppUserDetails principal) {
        return ResponseEntity.ok(bookingService.getByReference(reference, principal.getUser()));
    }

    @GetMapping("/api/customer/bookings")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<PageResponseDTO<BookingResponseDTO>> myBookings(
            @AuthenticationPrincipal AppUserDetails principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(bookingService.myBookings(principal.getId(), pageable));
    }

    @PutMapping("/api/bookings/{id}/cancel")
    public ResponseEntity<CancellationResponseDTO> cancel(@PathVariable Long id,
                                                           @RequestBody(required = false) CancellationRequest request,
                                                           @AuthenticationPrincipal AppUserDetails principal) {
        return ResponseEntity.ok(cancellationService.cancel(principal.getUser(), id, request));
    }

    @PatchMapping("/api/bookings/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'TOUR_OPERATOR')")
    public ResponseEntity<BookingResponseDTO> changeStatus(@PathVariable Long id, @Valid @RequestBody BookingStatusUpdateRequest request) {
        return ResponseEntity.ok(bookingService.changeStatus(id, request));
    }

    @GetMapping("/api/operator/packages/{packageId}/bookings")
    @PreAuthorize("hasAnyRole('ADMIN', 'TOUR_OPERATOR')")
    public ResponseEntity<List<BookingResponseDTO>> forPackage(@PathVariable Long packageId) {
        return ResponseEntity.ok(bookingService.forPackage(packageId));
    }

    @GetMapping("/api/admin/bookings")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BookingResponseDTO>> all() {
        return ResponseEntity.ok(bookingService.all());
    }
}
