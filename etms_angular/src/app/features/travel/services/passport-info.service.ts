import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { PassportInfo } from '../models/travel.models';

@Injectable({ providedIn: 'root' })
export class PassportInfoService {
  private readonly baseUrl = `${environment.apiBaseUrl}/passport-info`;

  constructor(private readonly http: HttpClient) {}

  getAll(): Observable<PassportInfo[]> {
    return this.http.get<PassportInfo[]>(this.baseUrl);
  }

  getByEmployee(employeeNumber: string): Observable<PassportInfo[]> {
    return this.http.get<PassportInfo[]>(`${this.baseUrl}/by-employee/${employeeNumber}`);
  }

  getExpired(): Observable<PassportInfo[]> {
    return this.http.get<PassportInfo[]>(`${this.baseUrl}/expired`);
  }
}
