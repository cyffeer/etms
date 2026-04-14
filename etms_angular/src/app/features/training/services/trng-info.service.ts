import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { TrngInfo, TrngInfoRequest } from '../models/training.models';

@Injectable({ providedIn: 'root' })
export class TrngInfoService {
  private url = `${environment.apiBaseUrl}/trng-info`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<TrngInfo[]> {
    return this.http.get<TrngInfo[]>(this.url);
  }

  getById(id: number): Observable<TrngInfo> {
    return this.http.get<TrngInfo>(`${this.url}/${id}`);
  }

  create(req: TrngInfoRequest): Observable<TrngInfo> {
    return this.http.post<TrngInfo>(this.url, req);
  }

  update(id: number, req: TrngInfoRequest): Observable<TrngInfo> {
    return this.http.put<TrngInfo>(`${this.url}/${id}`, req);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
