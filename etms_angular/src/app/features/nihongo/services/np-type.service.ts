import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { NpType } from '../models/nihongo.models';

@Injectable({ providedIn: 'root' })
export class NpTypeService {
  private readonly baseUrl = `${environment.apiBaseUrl}/np-types`;

  constructor(private readonly http: HttpClient) {}

  getAll(): Observable<NpType[]> {
    return this.http.get<NpType[]>(this.baseUrl);
  }

  getById(id: number): Observable<NpType> {
    return this.http.get<NpType>(`${this.baseUrl}/${id}`);
  }

  create(payload: { npTypeCode: string; npTypeName: string; active?: boolean }): Observable<NpType> {
    return this.http.post<NpType>(this.baseUrl, payload);
  }

  update(id: number, payload: { npTypeCode: string; npTypeName: string; active?: boolean }): Observable<NpType> {
    return this.http.put<NpType>(`${this.baseUrl}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
