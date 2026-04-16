import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import {
  ApiResponse,
  EmployeeListResult,
  EmployeePagedData,
  EmployeeRequest,
  EmployeeResponse
} from '../models/employee.model';

@Injectable({ providedIn: 'root' })
export class EmployeesService {
  private readonly baseUrl = `${environment.apiBaseUrl}/employees`;

  constructor(private http: HttpClient) {}

  getEmployees(page?: number, size?: number): Observable<EmployeeListResult> {
    let params = new HttpParams();
    if (page != null && size != null) {
      params = params.set('page', page).set('size', size);
    }

    return this.http
      .get<ApiResponse<EmployeeResponse[] | EmployeePagedData>>(this.baseUrl, { params })
      .pipe(
        map((res) => {
          const payload = res.data as EmployeeResponse[] | EmployeePagedData;
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

  searchEmployees(filters: {
    employeeNumber?: string | null;
    nameKeyword?: string | null;
    startDate?: string | null;
    endDate?: string | null;
  }): Observable<EmployeeResponse[]> {
    let params = new HttpParams();

    if (filters.employeeNumber?.trim()) {
      params = params.set('employeeNumber', filters.employeeNumber.trim());
    }
    if (filters.nameKeyword?.trim()) {
      params = params.set('nameKeyword', filters.nameKeyword.trim());
    }
    if (filters.startDate?.trim()) {
      params = params.set('startDate', filters.startDate.trim());
    }
    if (filters.endDate?.trim()) {
      params = params.set('endDate', filters.endDate.trim());
    }

    return this.http
      .get<ApiResponse<EmployeeResponse[]>>(`${this.baseUrl}/search`, { params })
      .pipe(map((res) => res.data ?? []));
  }

  getEmployeeById(employeeId: number): Observable<EmployeeResponse> {
    return this.http
      .get<ApiResponse<EmployeeResponse>>(`${this.baseUrl}/${employeeId}`)
      .pipe(map((res) => res.data));
  }

  createEmployee(request: EmployeeRequest): Observable<EmployeeResponse> {
    return this.http
      .post<ApiResponse<EmployeeResponse>>(this.baseUrl, request)
      .pipe(map((res) => res.data));
  }

  updateEmployee(employeeId: number, request: EmployeeRequest): Observable<EmployeeResponse> {
    return this.http
      .put<ApiResponse<EmployeeResponse>>(`${this.baseUrl}/${employeeId}`, request)
      .pipe(map((res) => res.data));
  }

  uploadPhoto(employeeId: number, file: File): Observable<EmployeeResponse> {
    const formData = new FormData();
    formData.append('file', file);

    return this.http
      .post<ApiResponse<EmployeeResponse>>(`${this.baseUrl}/${employeeId}/photo`, formData)
      .pipe(map((res) => res.data));
  }

  exportEmployees(format: 'xlsx' | 'pdf'): Observable<Blob> {
    const params = new HttpParams().set('format', format);
    return this.http.get(`${environment.apiBaseUrl}/reports/employees`, {
      params,
      responseType: 'blob'
    });
  }

  resolvePhotoUrl(photoUrl?: string | null): string | null {
    if (!photoUrl) {
      return null;
    }
    if (/^https?:\/\//i.test(photoUrl)) {
      return photoUrl;
    }
    const origin = environment.apiBaseUrl.replace(/\/api$/, '');
    return `${origin}${photoUrl}`;
  }

  deleteEmployee(employeeId: number): Observable<void> {
    return this.http
      .delete<ApiResponse<void>>(`${this.baseUrl}/${employeeId}`)
      .pipe(map(() => void 0));
  }
}
