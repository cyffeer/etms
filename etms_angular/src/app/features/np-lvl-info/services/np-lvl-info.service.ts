import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { NpLvlInfoRequest, NpLvlInfoResponse } from '../np-lvl-info.model';

@Injectable({ providedIn: 'root' })
export class NpLvlInfoService {
  private readonly baseUrl = `${environment.apiBaseUrl}/np-lvl-info`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<NpLvlInfoResponse[]> {
    return this.http.get<NpLvlInfoResponse[]>(this.baseUrl);
  }

  getById(id: number): Observable<NpLvlInfoResponse> {
    return this.http.get<NpLvlInfoResponse>(`${this.baseUrl}/${id}`);
  }

  create(payload: NpLvlInfoRequest): Observable<NpLvlInfoResponse> {
    return this.http.post<NpLvlInfoResponse>(this.baseUrl, payload);
  }

  update(id: number, payload: NpLvlInfoRequest): Observable<NpLvlInfoResponse> {
    return this.http.put<NpLvlInfoResponse>(`${this.baseUrl}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
