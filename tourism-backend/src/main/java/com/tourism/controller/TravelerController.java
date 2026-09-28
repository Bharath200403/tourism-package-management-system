package com.tourism.controller;

import com.tourism.dto.response.TravelerResponseDTO;
import com.tourism.mapper.DtoMapper;
import com.tourism.repository.TravelerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/operator/bookings")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'TOUR_OPERATOR')")
public class TravelerController {

    private final TravelerRepository travelerRepository;

    @GetMapping("/{bookingId}/travelers")
    public ResponseEntity<List<TravelerResponseDTO>> travelers(@PathVariable Long bookingId) {
        List<TravelerResponseDTO> list = travelerRepository.findByBookingId(bookingId).stream()
                .map(DtoMapper::toTravelerDTO).toList();
        return ResponseEntity.ok(list);
    }
}
