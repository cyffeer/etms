import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { LeaveType, LeaveTypeRequest } from '../models/leave-type.models';

@Injectable({ providedIn: 'root' })
export class LeaveTypeService {
  private readonly baseUrl = `${environment.apiBaseUrl}/leave-types`;

  constructor(private readonly http: HttpClient) {}

  getAll(keyword?: string | null): Observable<LeaveType[]> {
    let params = new HttpParams();
    if (keyword?.trim()) {
      params = params.set('keyword', keyword.trim());
    }
    return this.http.get<LeaveType[]>(this.baseUrl, { params });
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
