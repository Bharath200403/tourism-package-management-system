package com.tourism.controller;

import com.tourism.dto.request.ProfileUpdateRequest;
import com.tourism.dto.request.RegisterRequest;
import com.tourism.dto.response.UserResponseDTO;
import com.tourism.entity.enums.Role;
import com.tourism.entity.enums.UserStatus;
import com.tourism.security.AppUserDetails;
import com.tourism.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @GetMapping("/api/admin/customers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> customers() {
        return ResponseEntity.ok(userService.listByRole(Role.CUSTOMER));
    }

    @GetMapping("/api/admin/operators")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> operators() {
        return ResponseEntity.ok(userService.listByRole(Role.TOUR_OPERATOR));
    }

    @PostMapping("/api/admin/operators")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> createOperator(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.createStaffUser(request, Role.TOUR_OPERATOR));
    }

    @PostMapping("/api/admin/admins")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> createAdmin(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.createStaffUser(request, Role.ADMIN));
    }

    @PatchMapping("/api/admin/users/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> setStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(userService.setStatus(id, UserStatus.valueOf(status.toUpperCase())));
    }

    @PutMapping("/api/profile")
    public ResponseEntity<Void> updateProfile(@RequestBody ProfileUpdateRequest request, @AuthenticationPrincipal AppUserDetails principal) {
        userService.updateProfile(principal.getId(), request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/profile")
    public ResponseEntity<UserResponseDTO> myProfile(@AuthenticationPrincipal AppUserDetails principal) {
        return ResponseEntity.ok(userService.getById(principal.getId()));
    }
}
