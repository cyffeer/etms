import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../../models/api-response.model';
import { Leave, LeaveApi, LeaveRequest } from '../models/leave.models';

@Injectable({ providedIn: 'root' })
export class LeaveService {
  private readonly baseUrl = `${environment.apiBaseUrl}/leaves`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<Leave[]> {
    return this.http.get<ApiResponse<LeaveApi[]>>(this.baseUrl).pipe(
      map((res) => (res.data ?? []).map((x) => this.toUi(x)))
    );
  }

  search(filters: {
    employeeNumber?: string | null;
    leaveType?: string | null;
    status?: string | null;
  }): Observable<Leave[]> {
    let params = new HttpParams();

    if (filters.employeeNumber?.trim()) {
      params = params.set('employeeNumber', filters.employeeNumber.trim());
    }
    if (filters.leaveType?.trim()) {
      params = params.set('leaveType', filters.leaveType.trim());
    }
    if (filters.status?.trim()) {
      params = params.set('status', filters.status.trim());
    }

    return this.http.get<ApiResponse<LeaveApi[]>>(`${this.baseUrl}/search`, { params }).pipe(
      map((res) => (res.data ?? []).map((x) => this.toUi(x)))
    );
  }

  getById(id: number): Observable<Leave> {
    return this.http.get<ApiResponse<LeaveApi>>(`${this.baseUrl}/${id}`).pipe(
      map((res) => this.toUi(res.data))
    );
  }

  create(payload: LeaveRequest): Observable<Leave> {
    return this.http.post<ApiResponse<LeaveApi>>(this.baseUrl, this.toApiPayload(payload)).pipe(
      map((res) => this.toUi(res.data))
    );
  }

  update(id: number, payload: LeaveRequest): Observable<Leave> {
    return this.http.put<ApiResponse<LeaveApi>>(`${this.baseUrl}/${id}`, this.toApiPayload(payload)).pipe(
      map((res) => this.toUi(res.data))
    );
  }

  delete(id: number): Observable<void> {
    return this.http.delete<ApiResponse<void>>(`${this.baseUrl}/${id}`).pipe(map(() => void 0));
  }

  private toUi(x: LeaveApi): Leave {
    return {
      leaveRecordId: x.leaveRecordId,
      employeeNumber: x.employeeNumber,
      leaveType: x.leaveType,
      startDate: x.startDate,
      endDate: x.endDate,
      status: x.status ?? null,
      remarks: x.remarks ?? null,
    };
  }

  private toApiPayload(x: LeaveRequest): LeaveRequest {
    return {
      employeeNumber: x.employeeNumber?.trim(),
      leaveType: x.leaveType?.trim(),
      startDate: this.toDateOnly(x.startDate),
      endDate: this.toDateOnly(x.endDate),
      status: x.status ?? null,
      remarks: x.remarks ?? null,
    };
  }

  private toDateOnly(value: string): string {
    // expects date input value; keeps yyyy-MM-dd for backend
    return value?.slice(0, 10);
  }
}
