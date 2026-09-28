package com.tourism.controller;

import com.tourism.dto.request.ItineraryRequest;
import com.tourism.dto.request.PackageScheduleRequest;
import com.tourism.dto.request.TourPackageRequest;
import com.tourism.dto.response.PackageScheduleResponseDTO;
import com.tourism.dto.response.PageResponseDTO;
import com.tourism.dto.response.TourPackageResponseDTO;
import com.tourism.entity.enums.PackageStatus;
import com.tourism.security.AppUserDetails;
import com.tourism.service.PackageService;
import com.tourism.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/packages")
@RequiredArgsConstructor
public class PackageController {

    private final PackageService packageService;
    private final ScheduleService scheduleService;

    @GetMapping
    public ResponseEntity<PageResponseDTO<TourPackageResponseDTO>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long destinationId,
            @RequestParam(required = false) Integer minDuration,
            @RequestParam(required = false) Integer maxDuration,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        PackageStatus st = status != null ? PackageStatus.valueOf(status.toUpperCase()) : PackageStatus.ACTIVE;
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(packageService.search(name, destinationId, minDuration, maxDuration, minPrice, maxPrice, st, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TourPackageResponseDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(packageService.getById(id));
    }

    @GetMapping("/{id}/schedules")
    public ResponseEntity<List<PackageScheduleResponseDTO>> schedules(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.listForPackage(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TOUR_OPERATOR')")
    public ResponseEntity<TourPackageResponseDTO> create(@Valid @RequestBody TourPackageRequest request,
                                                          @AuthenticationPrincipal AppUserDetails principal) {
        return ResponseEntity.ok(packageService.create(request, principal.getUser()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TOUR_OPERATOR')")
    public ResponseEntity<TourPackageResponseDTO> update(@PathVariable Long id, @Valid @RequestBody TourPackageRequest request) {
        return ResponseEntity.ok(packageService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TOUR_OPERATOR')")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        packageService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/itinerary")
    @PreAuthorize("hasAnyRole('ADMIN', 'TOUR_OPERATOR')")
    public ResponseEntity<Void> addItineraryDay(@PathVariable Long id, @Valid @RequestBody ItineraryRequest request) {
        packageService.addItineraryDay(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/itinerary/{itineraryId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TOUR_OPERATOR')")
    public ResponseEntity<Void> removeItineraryDay(@PathVariable Long id, @PathVariable Long itineraryId) {
        packageService.removeItineraryDay(id, itineraryId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/schedules")
    @PreAuthorize("hasAnyRole('ADMIN', 'TOUR_OPERATOR')")
    public ResponseEntity<PackageScheduleResponseDTO> addSchedule(@PathVariable Long id, @Valid @RequestBody PackageScheduleRequest request) {
        return ResponseEntity.ok(scheduleService.create(id, request));
    }
}
