import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { AttendanceRequest, AttendanceResponse } from '../attendance.model';

@Injectable({ providedIn: 'root' })
export class AttendanceService {
  private readonly baseUrl = `${environment.apiBaseUrl}/attendance`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<AttendanceResponse[]> {
    return this.http.get<AttendanceResponse[]>(this.baseUrl);
  }

  search(filters: {
    employeeNumber?: string | null;
    year?: number | null;
    month?: number | null;
  }): Observable<AttendanceResponse[]> {
    let params = new HttpParams();

    if (filters.employeeNumber?.trim()) {
      params = params.set('employeeNumber', filters.employeeNumber.trim());
    }
    if (filters.year != null) {
      params = params.set('year', filters.year);
    }
    if (filters.month != null) {
      params = params.set('month', filters.month);
    }

    return this.http.get<AttendanceResponse[]>(`${this.baseUrl}/search`, { params });
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
}
