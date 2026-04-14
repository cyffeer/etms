import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ApiResponse, DepartmentRequest, DepartmentResponse } from '../models/department.model';

@Injectable({ providedIn: 'root' })
export class DepartmentsService {
  private readonly baseUrl = `${environment.apiBaseUrl}/departments`;

  constructor(private http: HttpClient) {}

  getDepartments(): Observable<DepartmentResponse[]> {
    return this.http
      .get<ApiResponse<DepartmentResponse[]>>(this.baseUrl)
      .pipe(map((res) => res.data ?? []));
  }

  searchDepartments(keyword?: string | null): Observable<DepartmentResponse[]> {
    let params = new HttpParams();
    if (keyword?.trim()) {
      params = params.set('keyword', keyword.trim());
    }

    return this.http
      .get<ApiResponse<DepartmentResponse[]>>(`${this.baseUrl}/search`, { params })
      .pipe(map((res) => res.data ?? []));
  }

  getDepartmentById(departmentId: number): Observable<DepartmentResponse> {
    return this.http
      .get<ApiResponse<DepartmentResponse>>(`${this.baseUrl}/${departmentId}`)
      .pipe(map((res) => res.data));
  }

  createDepartment(request: DepartmentRequest): Observable<DepartmentResponse> {
    return this.http
      .post<ApiResponse<DepartmentResponse>>(this.baseUrl, request)
      .pipe(map((res) => res.data));
  }

  updateDepartment(departmentId: number, request: DepartmentRequest): Observable<DepartmentResponse> {
    return this.http
      .put<ApiResponse<DepartmentResponse>>(`${this.baseUrl}/${departmentId}`, request)
      .pipe(map((res) => res.data));
  }

  deleteDepartment(departmentId: number): Observable<void> {
    return this.http
      .delete<ApiResponse<null>>(`${this.baseUrl}/${departmentId}`)
      .pipe(map(() => void 0));
  }
}
