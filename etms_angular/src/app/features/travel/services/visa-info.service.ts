import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { VisaInfo } from '../models/travel.models';

@Injectable({ providedIn: 'root' })
export class VisaInfoService {
  private readonly baseUrl = `${environment.apiBaseUrl}/visa-info`;

  constructor(private readonly http: HttpClient) {}

  getAll(): Observable<VisaInfo[]> {
    return this.http.get<VisaInfo[]>(this.baseUrl);
  }

  getByEmployee(employeeNumber: string): Observable<VisaInfo[]> {
    return this.http.get<VisaInfo[]>(`${this.baseUrl}/by-employee/${employeeNumber}`);
  }

  getExpired(): Observable<VisaInfo[]> {
    return this.http.get<VisaInfo[]>(`${this.baseUrl}/expired`);
  }

  getExpiring(days: number): Observable<VisaInfo[]> {
    return this.http.get<VisaInfo[]>(`${this.baseUrl}/expiring`, {
      params: { days },
    });
  }

  updateCancelFlag(employeeNumber: string, visaTypeId: number, cancelFlag: boolean): Observable<VisaInfo> {
    return this.http.patch<VisaInfo>(`${this.baseUrl}/${employeeNumber}/${visaTypeId}/cancel-flag`, {
      cancelFlag,
    });
  }
}
