package com.tourism.controller;

import com.tourism.dto.response.AuditLogResponseDTO;
import com.tourism.dto.response.PageResponseDTO;
import com.tourism.mapper.DtoMapper;
import com.tourism.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/audit-logs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AuditLogController {

    private final AuditLogRepository auditLogRepository;

    @GetMapping
    public ResponseEntity<PageResponseDTO<AuditLogResponseDTO>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        var result = auditLogRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size))
                .map(DtoMapper::toAuditDTO);
        return ResponseEntity.ok(PageResponseDTO.of(result));
    }
}
