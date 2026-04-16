import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../../models/api-response.model';
import { EmployeeResponse } from '../../employees/models/employee.model';

export interface EmployeeSelfUpdateRequest {
  firstName?: string | null;
  lastName?: string | null;
  email?: string | null;
}

@Injectable({ providedIn: 'root' })
export class EmployeeSelfService {
  private readonly baseUrl = `${environment.apiBaseUrl}/employees/me`;

  constructor(private readonly http: HttpClient) {}

  getMyProfile(): Observable<EmployeeResponse> {
    return this.http.get<ApiResponse<EmployeeResponse>>(this.baseUrl).pipe(map((res) => res.data));
  }

  updateMyProfile(request: EmployeeSelfUpdateRequest): Observable<EmployeeResponse> {
    return this.http.put<ApiResponse<EmployeeResponse>>(this.baseUrl, request).pipe(map((res) => res.data));
  }
}
