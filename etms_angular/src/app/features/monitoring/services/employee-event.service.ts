import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { EmployeeEventRequest, EmployeeEventResponse } from '../models/employee-event.model';

@Injectable({ providedIn: 'root' })
export class EmployeeEventService {
  private readonly baseUrl = `${environment.apiBaseUrl}/employee-events`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<EmployeeEventResponse[]> {
    return this.http.get<EmployeeEventResponse[]>(this.baseUrl);
  }

  search(filters: {
    employeeNumber?: string | null;
    eventType?: string | null;
    status?: string | null;
    keyword?: string | null;
    startDate?: string | null;
    endDate?: string | null;
  }): Observable<EmployeeEventResponse[]> {
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

    return this.http.get<EmployeeEventResponse[]>(`${this.baseUrl}/search`, { params });
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
}
