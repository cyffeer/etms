package org.fujitsu.codes.etms.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.fujitsu.codes.etms.model.dao.AuditLogDao;
import org.fujitsu.codes.etms.model.data.AuditLog;
import org.fujitsu.codes.etms.model.dto.ApiResponse;
import org.fujitsu.codes.etms.model.dto.AuditLogResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/audit-logs")
public class AuditLogRestController {

    private final AuditLogDao auditLogDao;

    public AuditLogRestController(AuditLogDao auditLogDao) {
        this.auditLogDao = auditLogDao;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> search(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) LocalDate loggedFrom,
            @RequestParam(required = false) LocalDate loggedTo,
            @RequestParam(required = false, defaultValue = "100") Integer limit) {

        LocalDateTime from = loggedFrom == null ? null : loggedFrom.atStartOfDay();
        LocalDateTime to = loggedTo == null ? null : loggedTo.plusDays(1).atStartOfDay().minusNanos(1);

        List<AuditLogResponse> data = auditLogDao.search(username, entityType, action, from, to, limit == null ? 100 : limit)
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(ApiResponse.success("Audit logs fetched successfully", data));
    }

    private AuditLogResponse toResponse(AuditLog entity) {
        AuditLogResponse response = new AuditLogResponse();
        response.setAuditLogId(entity.getAuditLogId());
        response.setUsername(entity.getUsername());
        response.setUserRole(entity.getUserRole());
        response.setAction(entity.getAction());
        response.setEntityType(entity.getEntityType());
        response.setEntityId(entity.getEntityId());
        response.setRequestMethod(entity.getRequestMethod());
        response.setRequestPath(entity.getRequestPath());
        response.setDescription(entity.getDescription());
        response.setLoggedAt(entity.getLoggedAt());
        return response;
    }
}
