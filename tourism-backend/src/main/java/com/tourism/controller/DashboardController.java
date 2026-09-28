package com.tourism.controller;

import com.tourism.dto.response.AdminDashboardDTO;
import com.tourism.dto.response.CustomerDashboardDTO;
import com.tourism.dto.response.OperatorDashboardDTO;
import com.tourism.security.AppUserDetails;
import com.tourism.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/api/customer/dashboard")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CustomerDashboardDTO> customer(@AuthenticationPrincipal AppUserDetails principal) {
        return ResponseEntity.ok(dashboardService.customerDashboard(principal.getId()));
    }

    @GetMapping("/api/operator/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'TOUR_OPERATOR')")
    public ResponseEntity<OperatorDashboardDTO> operator(@AuthenticationPrincipal AppUserDetails principal) {
        return ResponseEntity.ok(dashboardService.operatorDashboard(principal.getId()));
    }

    @GetMapping("/api/admin/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminDashboardDTO> admin() {
        return ResponseEntity.ok(dashboardService.adminDashboard());
    }
}
