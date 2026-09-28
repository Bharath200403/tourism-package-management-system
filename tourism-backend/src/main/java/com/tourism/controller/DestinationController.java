package com.tourism.controller;

import com.tourism.dto.request.DestinationRequest;
import com.tourism.dto.response.DestinationResponseDTO;
import com.tourism.dto.response.PageResponseDTO;
import com.tourism.entity.enums.PackageStatus;
import com.tourism.service.DestinationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/destinations")
@RequiredArgsConstructor
public class DestinationController {

    private final DestinationService destinationService;

    @GetMapping
    public ResponseEntity<PageResponseDTO<DestinationResponseDTO>> list(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PackageStatus st = status != null ? PackageStatus.valueOf(status.toUpperCase()) : null;
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(destinationService.list(st, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DestinationResponseDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(destinationService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TOUR_OPERATOR')")
    public ResponseEntity<DestinationResponseDTO> create(@Valid @RequestBody DestinationRequest request) {
        return ResponseEntity.ok(destinationService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TOUR_OPERATOR')")
    public ResponseEntity<DestinationResponseDTO> update(@PathVariable Long id, @Valid @RequestBody DestinationRequest request) {
        return ResponseEntity.ok(destinationService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        destinationService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
