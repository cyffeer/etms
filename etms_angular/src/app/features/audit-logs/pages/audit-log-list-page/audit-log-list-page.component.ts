import { Component, OnInit } from '@angular/core';
import { AuditLog } from '../../models/audit-log.model';
import { AuditLogService } from '../../services/audit-log.service';

@Component({
  selector: 'app-audit-log-list-page',
  templateUrl: './audit-log-list-page.component.html'
})
export class AuditLogListPageComponent implements OnInit {
  rows: AuditLog[] = [];
  loading = false;
  error = '';

  readonly actions = ['LOGIN', 'CREATE', 'UPDATE', 'DELETE'];
  readonly entityTypes = ['AUTH', 'EMPLOYEE_EVENT'];

  filters = {
    username: '',
    entityType: '',
    action: '',
    loggedFrom: '',
    loggedTo: '',
    limit: 100
  };

  constructor(private auditLogService: AuditLogService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = '';

    this.auditLogService.search(this.filters).subscribe({
      next: (rows) => {
        this.rows = rows;
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.error?.message || err?.message || 'Failed to load audit logs.';
        this.loading = false;
      }
    });
  }

  resetFilters(): void {
    this.filters = {
      username: '',
      entityType: '',
      action: '',
      loggedFrom: '',
      loggedTo: '',
      limit: 100
    };
    this.load();
  }
}
