import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../../environments/environment';
import { TrngHist, TrngHistRequest } from '../models/training.models';

@Injectable({ providedIn: 'root' })
export class TrngHistoryService {
  private url = `${environment.apiBaseUrl}/trng-history`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<TrngHist[]> {
    return this.http.get<{ data?: TrngHist[] }>(this.url).pipe(map(r => r.data ?? []));
  }

  getByEmployee(employeeNumber: string): Observable<TrngHist[]> {
    return this.http.get<TrngHist[]>(`${this.url}/by-employee/${employeeNumber}`);
  }

  getByTraining(trainingId: number): Observable<TrngHist[]> {
    return this.http.get<TrngHist[]>(`${this.url}/by-training/${trainingId}`);
  }

  assign(req: TrngHistRequest): Observable<TrngHist> {
    return this.http.post<TrngHist>(`${this.url}/assign`, req);
  }

  delete(trngId: number, employeeNumber: string): Observable<void> {
    return this.http.delete<void>(`${this.url}/${trngId}/${employeeNumber}`);
  }

  exportReport(format: 'xlsx' | 'pdf', employeeNumber?: string): Observable<Blob> {
    let params = new HttpParams().set('format', format);
    if (employeeNumber?.trim()) {
      params = params.set('employeeNumber', employeeNumber.trim());
    }
    return this.http.get(`${environment.apiBaseUrl}/reports/training-history`, {
      params,
      responseType: 'blob'
    });
  }
}
