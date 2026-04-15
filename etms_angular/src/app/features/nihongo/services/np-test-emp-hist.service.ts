import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { NpTestEmpHist } from '../models/nihongo.models';

@Injectable({ providedIn: 'root' })
export class NpTestEmpHistService {
  private readonly baseUrl = `${environment.apiBaseUrl}/np-test-emp-hist`;

  constructor(private readonly http: HttpClient) {}

  getAll(): Observable<NpTestEmpHist[]> {
    return this.http.get<NpTestEmpHist[]>(this.baseUrl);
  }

  search(filters: {
    employeeNumber?: string | null;
    passedOnly?: boolean;
    mostRecentOnly?: boolean;
  }): Observable<NpTestEmpHist[]> {
    let params = new HttpParams();

    if (filters.employeeNumber?.trim()) {
      params = params.set('employeeNumber', filters.employeeNumber.trim());
    }

    params = params.set('passedOnly', String(!!filters.passedOnly));
    params = params.set('mostRecentOnly', String(!!filters.mostRecentOnly));

    return this.http.get<NpTestEmpHist[]>(`${this.baseUrl}/search`, { params });
  }
}
