import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import {
  AttendanceListResult,
  AttendancePagedData,
  AttendanceRequest,
  AttendanceResponse
} from '../attendance.model';

@Injectable({ providedIn: 'root' })
export class AttendanceService {
  private readonly baseUrl = `${environment.apiBaseUrl}/attendance`;
  private readonly reportBaseUrl = `${environment.apiBaseUrl}/reports`;

  constructor(private http: HttpClient) {}

  getAll(page?: number, size?: number): Observable<AttendanceListResult> {
    let params = new HttpParams();
    if (page != null && size != null) {
      params = params.set('page', page).set('size', size);
    }

    return this.http.get<{ success: boolean; data: AttendanceResponse[] | AttendancePagedData }>(this.baseUrl, { params }).pipe(
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

  search(filters: {
    employeeNumber?: string | null;
    employeeName?: string | null;
    designation?: string | null;
    officeName?: string | null;
    year?: number | null;
    month?: number | null;
  }, page?: number, size?: number): Observable<AttendanceListResult> {
    let params = new HttpParams();

    if (filters.employeeNumber?.trim()) {
      params = params.set('employeeNumber', filters.employeeNumber.trim());
    }
    if (filters.employeeName?.trim()) {
      params = params.set('employeeName', filters.employeeName.trim());
    }
    if (filters.designation?.trim()) {
      params = params.set('designation', filters.designation.trim());
    }
    if (filters.officeName?.trim()) {
      params = params.set('officeName', filters.officeName.trim());
    }
    if (filters.year != null) {
      params = params.set('year', filters.year);
    }
    if (filters.month != null) {
      params = params.set('month', filters.month);
    }
    if (page != null && size != null) {
      params = params.set('page', page).set('size', size);
    }

    return this.http.get<{ success: boolean; data: AttendanceResponse[] | AttendancePagedData }>(`${this.baseUrl}/search`, { params }).pipe(
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

  getById(id: number): Observable<AttendanceResponse> {
    return this.http.get<AttendanceResponse>(`${this.baseUrl}/${id}`);
  }

  create(payload: AttendanceRequest): Observable<AttendanceResponse> {
    return this.http.post<AttendanceResponse>(this.baseUrl, payload);
  }

  update(id: number, payload: AttendanceRequest): Observable<AttendanceResponse> {
    return this.http.put<AttendanceResponse>(`${this.baseUrl}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  exportReport(format: 'xlsx' | 'pdf', filters: {
    employeeNumber?: string | null;
    employeeName?: string | null;
    designation?: string | null;
    officeName?: string | null;
    year?: number | null;
    month?: number | null;
  }): Observable<Blob> {
    let params = new HttpParams().set('format', format);

    if (filters.employeeNumber?.trim()) {
      params = params.set('employeeNumber', filters.employeeNumber.trim());
    }
    if (filters.year != null) {
      params = params.set('year', filters.year);
    }
    if (filters.month != null) {
      params = params.set('month', filters.month);
    }

    return this.http.get(`${this.reportBaseUrl}/attendance`, {
      params,
      responseType: 'blob'
    });
  }
}
