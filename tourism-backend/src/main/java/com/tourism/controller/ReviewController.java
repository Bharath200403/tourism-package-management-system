package com.tourism.controller;

import com.tourism.dto.request.ReviewRequest;
import com.tourism.dto.response.PageResponseDTO;
import com.tourism.dto.response.ReviewResponseDTO;
import com.tourism.entity.enums.ReviewStatus;
import com.tourism.security.AppUserDetails;
import com.tourism.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/api/reviews")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ReviewResponseDTO> submit(@Valid @RequestBody ReviewRequest request, @AuthenticationPrincipal AppUserDetails principal) {
        return ResponseEntity.ok(reviewService.submit(principal.getUser(), request));
    }

    @GetMapping("/api/packages/{id}/reviews")
    public ResponseEntity<PageResponseDTO<ReviewResponseDTO>> forPackage(@PathVariable Long id,
                                                                          @RequestParam(defaultValue = "0") int page,
                                                                          @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(reviewService.forPackage(id, PageRequest.of(page, size)));
    }

    @PatchMapping("/api/admin/reviews/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> moderate(@PathVariable Long id, @RequestParam String status) {
        reviewService.moderate(id, ReviewStatus.valueOf(status.toUpperCase()));
        return ResponseEntity.ok().build();
    }
}
