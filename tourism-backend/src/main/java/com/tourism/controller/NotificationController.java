package com.tourism.controller;

import com.tourism.dto.response.NotificationResponseDTO;
import com.tourism.mapper.DtoMapper;
import com.tourism.security.AppUserDetails;
import com.tourism.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponseDTO>> mine(@AuthenticationPrincipal AppUserDetails principal) {
        List<NotificationResponseDTO> list = notificationService.getForUser(principal.getId()).stream()
                .map(DtoMapper::toNotificationDTO).toList();
        return ResponseEntity.ok(list);
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markRead(@PathVariable Long id, @AuthenticationPrincipal AppUserDetails principal) {
        notificationService.markRead(id, principal.getId());
        return ResponseEntity.ok().build();
    }
}
