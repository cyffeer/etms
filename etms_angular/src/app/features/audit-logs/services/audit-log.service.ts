import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../../models/api-response.model';
import { AuditLog } from '../models/audit-log.model';

@Injectable({ providedIn: 'root' })
export class AuditLogService {
  private readonly baseUrl = `${environment.apiBaseUrl}/audit-logs`;

  constructor(private http: HttpClient) {}

  search(filters: {
    username?: string | null;
    entityType?: string | null;
    action?: string | null;
    loggedFrom?: string | null;
    loggedTo?: string | null;
    limit?: number | null;
  }): Observable<AuditLog[]> {
    let params = new HttpParams();

    if (filters.username?.trim()) {
      params = params.set('username', filters.username.trim());
    }
    if (filters.entityType?.trim()) {
      params = params.set('entityType', filters.entityType.trim());
    }
    if (filters.action?.trim()) {
      params = params.set('action', filters.action.trim());
    }
    if (filters.loggedFrom?.trim()) {
      params = params.set('loggedFrom', filters.loggedFrom.trim());
    }
    if (filters.loggedTo?.trim()) {
      params = params.set('loggedTo', filters.loggedTo.trim());
    }
    if (filters.limit != null) {
      params = params.set('limit', String(filters.limit));
    }

    return this.http
      .get<ApiResponse<AuditLog[]>>(this.baseUrl, { params })
      .pipe(map((res) => res.data ?? []));
  }
}
