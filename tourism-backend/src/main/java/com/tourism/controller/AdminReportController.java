package com.tourism.controller;

import com.tourism.dto.response.*;
import com.tourism.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminReportController {

    private final ReportService reportService;

    @GetMapping("/bookings")
    public ResponseEntity<BookingReportDTO> bookings() {
        return ResponseEntity.ok(reportService.bookingReport());
    }

    @GetMapping("/revenue")
    public ResponseEntity<RevenueReportDTO> revenue() {
        return ResponseEntity.ok(reportService.revenueReport());
    }

    @GetMapping("/packages")
    public ResponseEntity<List<PackagePerformanceDTO>> packages() {
        return ResponseEntity.ok(reportService.packagePerformanceReport());
    }

    @GetMapping("/customers")
    public ResponseEntity<List<CustomerReportDTO>> customers() {
        return ResponseEntity.ok(reportService.customerReport());
    }

    @GetMapping("/cancellations")
    public ResponseEntity<CancellationReportDTO> cancellations() {
        return ResponseEntity.ok(reportService.cancellationReport());
    }

    @GetMapping("/occupancy")
    public ResponseEntity<List<OccupancyReportDTO>> occupancy() {
        return ResponseEntity.ok(reportService.occupancyReport());
    }
}
