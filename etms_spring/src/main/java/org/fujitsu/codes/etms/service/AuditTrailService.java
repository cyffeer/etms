package org.fujitsu.codes.etms.service;

import java.time.LocalDateTime;

import org.fujitsu.codes.etms.model.dao.AuditLogDao;
import org.fujitsu.codes.etms.model.data.AuditLog;
import org.fujitsu.codes.etms.model.data.Login;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class AuditTrailService {

    private static final Logger log = LoggerFactory.getLogger(AuditTrailService.class);

    private final AuditLogDao auditLogDao;

    public AuditTrailService(AuditLogDao auditLogDao) {
        this.auditLogDao = auditLogDao;
    }

    public void log(Login user, String action, String entityType, String entityId, String description, HttpServletRequest request) {
        try {
            AuditLog entity = new AuditLog();
            entity.setUsername(user == null || user.getUsername() == null ? "anonymous" : user.getUsername());
            entity.setUserRole(user == null || user.getRole() == null ? "UNKNOWN" : user.getRole().name());
            entity.setAction(safeValue(action, "VIEW"));
            entity.setEntityType(safeValue(entityType, "UNKNOWN"));
            entity.setEntityId(entityId);
            entity.setRequestMethod(request == null ? "SYSTEM" : safeValue(request.getMethod(), "SYSTEM"));
            entity.setRequestPath(request == null ? "system" : safeValue(request.getRequestURI(), "system"));
            entity.setDescription(description);
            entity.setLoggedAt(LocalDateTime.now());
            auditLogDao.save(entity);
        } catch (RuntimeException ex) {
            log.warn("Audit trail write skipped: {}", ex.getMessage());
        }
    }

    private String safeValue(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }
}
