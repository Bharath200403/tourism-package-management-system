package com.tourism.controller;

import com.tourism.dto.request.PackageScheduleRequest;
import com.tourism.dto.response.PackageScheduleResponseDTO;
import com.tourism.entity.enums.ScheduleStatus;
import com.tourism.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'TOUR_OPERATOR')")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PutMapping("/{id}")
    public ResponseEntity<PackageScheduleResponseDTO> update(@PathVariable Long id, @Valid @RequestBody PackageScheduleRequest request) {
        return ResponseEntity.ok(scheduleService.update(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> setStatus(@PathVariable Long id, @RequestParam String status) {
        scheduleService.setStatus(id, ScheduleStatus.valueOf(status.toUpperCase()));
        return ResponseEntity.ok().build();
    }
}
