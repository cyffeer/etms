export interface AuditLog {
  auditLogId: number;
  username: string;
  userRole: string;
  action: string;
  entityType: string;
  entityId?: string | null;
  requestMethod: string;
  requestPath: string;
  description?: string | null;
  loggedAt: string;
}
