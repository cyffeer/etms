import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { LeaveType, LeaveTypeRequest } from '../models/leave-type.models';
import { ApiResponse } from '../../../models/api-response.model';
import { LeaveTypeListResult, LeaveTypePagedData } from '../models/leave-type.models';

@Injectable({ providedIn: 'root' })
export class LeaveTypeService {
  private readonly baseUrl = `${environment.apiBaseUrl}/leave-types`;

  constructor(private readonly http: HttpClient) {}

  getAll(filters?: {
    leaveTypeId?: number | null;
    keyword?: string | null;
  }, page?: number, size?: number): Observable<LeaveTypeListResult> {
    let params = new HttpParams();
    if (filters?.leaveTypeId != null) {
      params = params.set('leaveTypeId', filters.leaveTypeId);
    }
    if (filters?.keyword?.trim()) {
      params = params.set('keyword', filters.keyword.trim());
    }
    if (page != null && size != null) {
      params = params.set('page', page).set('size', size);
    }
    return this.http.get<ApiResponse<LeaveType[] | LeaveTypePagedData>>(this.baseUrl, { params }).pipe(
      map((res) => {
        const payload = res.data;
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

  getById(id: number): Observable<LeaveType> {
    return this.http.get<LeaveType>(`${this.baseUrl}/${id}`);
  }

  create(payload: LeaveTypeRequest): Observable<LeaveType> {
    return this.http.post<LeaveType>(this.baseUrl, payload);
  }

  update(id: number, payload: LeaveTypeRequest): Observable<LeaveType> {
    return this.http.put<LeaveType>(`${this.baseUrl}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
