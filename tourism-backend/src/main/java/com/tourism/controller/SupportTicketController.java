package com.tourism.controller;

import com.tourism.dto.request.SupportTicketRequest;
import com.tourism.dto.request.SupportTicketUpdateRequest;
import com.tourism.dto.response.SupportTicketResponseDTO;
import com.tourism.security.AppUserDetails;
import com.tourism.service.SupportTicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SupportTicketController {

    private final SupportTicketService supportTicketService;

    @PostMapping("/api/support-tickets")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<SupportTicketResponseDTO> create(@Valid @RequestBody SupportTicketRequest request, @AuthenticationPrincipal AppUserDetails principal) {
        return ResponseEntity.ok(supportTicketService.create(principal.getUser(), request));
    }

    @GetMapping("/api/customer/support-tickets")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<SupportTicketResponseDTO>> mine(@AuthenticationPrincipal AppUserDetails principal) {
        return ResponseEntity.ok(supportTicketService.mine(principal.getId()));
    }

    @GetMapping("/api/admin/support-tickets")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SupportTicketResponseDTO>> all() {
        return ResponseEntity.ok(supportTicketService.all());
    }

    @PatchMapping("/api/admin/support-tickets/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SupportTicketResponseDTO> update(@PathVariable Long id, @Valid @RequestBody SupportTicketUpdateRequest request) {
        return ResponseEntity.ok(supportTicketService.update(id, request));
    }
}
