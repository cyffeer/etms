import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { DepartmentMemberRequest, DepartmentMemberResponse } from '../department-member.model';

@Injectable({ providedIn: 'root' })
export class DepartmentMemberService {
  private readonly baseUrl = `${environment.apiBaseUrl}/dept-members`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<DepartmentMemberResponse[]> {
    return this.http.get<DepartmentMemberResponse[]>(this.baseUrl);
  }

  search(filters: {
    departmentCode?: string | null;
    employeeNumber?: string | null;
  }): Observable<DepartmentMemberResponse[]> {
    let params = new HttpParams();

    if (filters.departmentCode?.trim()) {
      params = params.set('departmentCode', filters.departmentCode.trim());
    }
    if (filters.employeeNumber?.trim()) {
      params = params.set('employeeNumber', filters.employeeNumber.trim());
    }

    return this.http.get<DepartmentMemberResponse[]>(`${this.baseUrl}/search`, { params });
  }

  getById(id: number): Observable<DepartmentMemberResponse> {
    return this.http.get<DepartmentMemberResponse>(`${this.baseUrl}/${id}`);
  }

  create(payload: DepartmentMemberRequest): Observable<DepartmentMemberResponse> {
    return this.http.post<DepartmentMemberResponse>(this.baseUrl, payload);
  }

  update(id: number, payload: DepartmentMemberRequest): Observable<DepartmentMemberResponse> {
    return this.http.put<DepartmentMemberResponse>(`${this.baseUrl}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
