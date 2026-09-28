package com.tourism.service;

import com.tourism.entity.AuditLog;
import com.tourism.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    /**
     * Records an important action. Never pass passwords, tokens or payment
     * credentials in {@code details}.
     */
    public void log(String action, String entityType, String entityId, String details) {
        AuditLog log = new AuditLog();
        log.setActorUsername(currentUsername());
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setDetails(details);
        auditLogRepository.save(log);
    }

    private String currentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null && auth.isAuthenticated()) ? auth.getName() : "SYSTEM";
    }
}
