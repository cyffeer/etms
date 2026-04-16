import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../../models/api-response.model';
import { NotificationItem, NotificationListResult, NotificationPagedData } from '../models/notification.model';

@Injectable({ providedIn: 'root' })
export class NotificationsService {
  private readonly baseUrl = `${environment.apiBaseUrl}/notifications`;

  constructor(private http: HttpClient) {}

  getAll(filters: {
    severity?: string | null;
    type?: string | null;
    limit?: number | null;
  }, page?: number, size?: number): Observable<NotificationListResult> {
    let params = new HttpParams();
    if (filters.severity?.trim()) {
      params = params.set('severity', filters.severity.trim());
    }
    if (filters.type?.trim()) {
      params = params.set('type', filters.type.trim());
    }
    if (filters.limit != null) {
      params = params.set('limit', filters.limit);
    }
    if (page != null && size != null) {
      params = params.set('page', page).set('size', size);
    }

    return this.http.get<ApiResponse<NotificationItem[] | NotificationPagedData>>(this.baseUrl, { params }).pipe(
      map((res) => {
        const payload = res.data ?? [];
        if (Array.isArray(payload)) {
          return { items: payload };
        }
        return {
          items: payload.data,
          page: payload.page,
          size: payload.size,
          totalElements: payload.totalElements,
          totalPages: payload.totalPages
        };
      })
    );
  }
}
