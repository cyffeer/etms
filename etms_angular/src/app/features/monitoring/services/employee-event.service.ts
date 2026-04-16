import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import {
  EmployeeEventListResult,
  EmployeeEventPagedData,
  EmployeeEventRequest,
  EmployeeEventResponse,
  EmployeeEventSummaryResponse
} from '../models/employee-event.model';

@Injectable({ providedIn: 'root' })
export class EmployeeEventService {
  private readonly baseUrl = `${environment.apiBaseUrl}/employee-events`;

  constructor(private http: HttpClient) {}

  getAll(page?: number, size?: number): Observable<EmployeeEventListResult> {
    let params = new HttpParams();
    if (page != null && size != null) {
      params = params.set('page', page).set('size', size);
    }

    return this.http.get<{ success: boolean; data: EmployeeEventResponse[] | EmployeeEventPagedData }>(this.baseUrl, { params })
      .pipe(
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
    eventType?: string | null;
    status?: string | null;
    keyword?: string | null;
    startDate?: string | null;
    endDate?: string | null;
  }, page?: number, size?: number): Observable<EmployeeEventListResult> {
    let params = new HttpParams();

    if (filters.employeeNumber?.trim()) {
      params = params.set('employeeNumber', filters.employeeNumber.trim());
    }
    if (filters.eventType?.trim()) {
      params = params.set('eventType', filters.eventType.trim());
    }
    if (filters.status?.trim()) {
      params = params.set('status', filters.status.trim());
    }
    if (filters.keyword?.trim()) {
      params = params.set('keyword', filters.keyword.trim());
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

    return this.http.get<{ success: boolean; data: EmployeeEventResponse[] | EmployeeEventPagedData }>(`${this.baseUrl}/search`, { params })
      .pipe(
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

  getById(employeeEventId: number): Observable<EmployeeEventResponse> {
    return this.http.get<EmployeeEventResponse>(`${this.baseUrl}/${employeeEventId}`);
  }

  create(payload: EmployeeEventRequest): Observable<EmployeeEventResponse> {
    return this.http.post<EmployeeEventResponse>(this.baseUrl, payload);
  }

  update(employeeEventId: number, payload: EmployeeEventRequest): Observable<EmployeeEventResponse> {
    return this.http.put<EmployeeEventResponse>(`${this.baseUrl}/${employeeEventId}`, payload);
  }

  delete(employeeEventId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${employeeEventId}`);
  }

  getSummary(): Observable<EmployeeEventSummaryResponse> {
    return this.http.get<{ success: boolean; data: EmployeeEventSummaryResponse }>(`${this.baseUrl}/summary`)
      .pipe(map((res) => res.data));
  }

  getCategory(eventType: string, page?: number, size?: number): Observable<EmployeeEventListResult> {
    let params = new HttpParams();
    if (page != null && size != null) {
      params = params.set('page', page).set('size', size);
    }

    return this.http.get<{ success: boolean; data: EmployeeEventResponse[] | EmployeeEventPagedData }>(
      `${this.baseUrl}/category/${encodeURIComponent(eventType)}`,
      { params }
    ).pipe(
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

  searchCategory(
    eventType: string,
    filters: {
      employeeNumber?: string | null;
      status?: string | null;
      keyword?: string | null;
      startDate?: string | null;
      endDate?: string | null;
    },
    page?: number,
    size?: number
  ): Observable<EmployeeEventListResult> {
    let params = new HttpParams();

    if (filters.employeeNumber?.trim()) {
      params = params.set('employeeNumber', filters.employeeNumber.trim());
    }
    if (filters.status?.trim()) {
      params = params.set('status', filters.status.trim());
    }
    if (filters.keyword?.trim()) {
      params = params.set('keyword', filters.keyword.trim());
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

    return this.http.get<{ success: boolean; data: EmployeeEventResponse[] | EmployeeEventPagedData }>(
      `${this.baseUrl}/category/${encodeURIComponent(eventType)}/search`,
      { params }
    ).pipe(
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
}
