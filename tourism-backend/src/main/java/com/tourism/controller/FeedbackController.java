package com.tourism.controller;

import com.tourism.dto.request.FeedbackRequest;
import com.tourism.dto.response.FeedbackResponseDTO;
import com.tourism.security.AppUserDetails;
import com.tourism.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping("/api/feedback")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<FeedbackResponseDTO> submit(@Valid @RequestBody FeedbackRequest request, @AuthenticationPrincipal AppUserDetails principal) {
        return ResponseEntity.ok(feedbackService.submit(principal.getUser(), request));
    }

    @GetMapping("/api/customer/feedback")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<FeedbackResponseDTO>> mine(@AuthenticationPrincipal AppUserDetails principal) {
        return ResponseEntity.ok(feedbackService.mine(principal.getId()));
    }

    @GetMapping("/api/admin/feedback")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FeedbackResponseDTO>> all() {
        return ResponseEntity.ok(feedbackService.all());
    }
}
