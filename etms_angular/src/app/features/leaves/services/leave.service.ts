import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../../models/api-response.model';
import { Leave, LeaveApi, LeaveBalance, LeaveBalanceApi, LeaveListResult, LeavePagedData, LeaveRequest } from '../models/leave.models';

@Injectable({ providedIn: 'root' })
export class LeaveService {
  private readonly baseUrl = `${environment.apiBaseUrl}/leaves`;
  private readonly reportBaseUrl = `${environment.apiBaseUrl}/reports`;

  constructor(private http: HttpClient) {}

  getAll(page?: number, size?: number): Observable<LeaveListResult> {
    let params = new HttpParams();
    if (page != null && size != null) {
      params = params.set('page', page).set('size', size);
    }

    return this.http.get<ApiResponse<LeaveApi[] | LeavePagedData>>(this.baseUrl, { params }).pipe(
      map((res) => {
        const payload = res.data ?? [];
        if (Array.isArray(payload)) {
          return { items: payload.map((x) => this.toUi(x)) };
        }
        return {
          items: payload.data.map((x) => this.toUi(x)),
          page: payload.page,
          size: payload.size,
          totalElements: payload.totalElements,
          totalPages: payload.totalPages
        };
      })
    );
  }

  search(filters: {
    employeeNumber?: string | null;
    leaveType?: string | null;
    status?: string | null;
    startDate?: string | null;
    endDate?: string | null;
  }, page?: number, size?: number): Observable<LeaveListResult> {
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
    if (filters.startDate?.trim()) {
      params = params.set('startDate', filters.startDate.trim());
    }
    if (filters.endDate?.trim()) {
      params = params.set('endDate', filters.endDate.trim());
    }
    if (page != null && size != null) {
      params = params.set('page', page).set('size', size);
    }

    return this.http.get<ApiResponse<LeaveApi[] | LeavePagedData>>(`${this.baseUrl}/search`, { params }).pipe(
      map((res) => {
        const payload = res.data ?? [];
        if (Array.isArray(payload)) {
          return { items: payload.map((x) => this.toUi(x)) };
        }
        return {
          items: payload.data.map((x) => this.toUi(x)),
          page: payload.page,
          size: payload.size,
          totalElements: payload.totalElements,
          totalPages: payload.totalPages
        };
      })
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

  exportReport(format: 'xlsx' | 'pdf', filters: {
    employeeNumber?: string | null;
    leaveType?: string | null;
    status?: string | null;
    startDate?: string | null;
    endDate?: string | null;
  }): Observable<Blob> {
    let params = new HttpParams().set('format', format);
    if (filters.employeeNumber?.trim()) {
      params = params.set('employeeNumber', filters.employeeNumber.trim());
    }
    if (filters.leaveType?.trim()) {
      params = params.set('leaveType', filters.leaveType.trim());
    }
    if (filters.status?.trim()) {
      params = params.set('status', filters.status.trim());
    }
    if (filters.startDate?.trim()) {
      params = params.set('startDate', filters.startDate.trim());
    }
    if (filters.endDate?.trim()) {
      params = params.set('endDate', filters.endDate.trim());
    }

    return this.http.get(`${this.reportBaseUrl}/leaves`, {
      params,
      responseType: 'blob'
    });
  }

  getBalances(employeeNumber?: string | null, year?: number | null): Observable<LeaveBalance[]> {
    let params = new HttpParams();
    if (employeeNumber?.trim()) {
      params = params.set('employeeNumber', employeeNumber.trim());
    }
    if (year != null) {
      params = params.set('year', year);
    }

    return this.http.get<ApiResponse<LeaveBalanceApi[]>>(`${this.baseUrl}/balances`, { params }).pipe(
      map((res) => (res.data ?? []).map((x) => this.toBalanceUi(x)))
    );
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
      startDate: this.toDateOnly(x.startDate) ?? '',
      endDate: this.toDateOnly(x.endDate),
      status: x.status ?? null,
      remarks: x.remarks ?? null,
    };
  }

  private toBalanceUi(x: LeaveBalanceApi): LeaveBalance {
    return {
      employeeNumber: x.employeeNumber,
      employeeName: x.employeeName,
      year: x.year,
      leaveTypeCode: x.leaveTypeCode,
      leaveTypeName: x.leaveTypeName,
      annualEntitlementDays: x.annualEntitlementDays,
      approvedDaysUsed: x.approvedDaysUsed,
      pendingDays: x.pendingDays,
      remainingDays: x.remainingDays,
    };
  }

  private toDateOnly(value?: string | null): string | null {
    // expects date input value; keeps yyyy-MM-dd for backend
    const normalized = value?.trim();
    if (!normalized) {
      return null;
    }
    return normalized.slice(0, 10);
  }
}
