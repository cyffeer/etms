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

  uploadCertificate(id: number, file: File): Observable<{ message: string; certificatePath: string }> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<{ message: string; certificatePath: string }>(`${this.url}/${id}/certificate`, formData);
  }

  downloadCertificate(id: number): Observable<Blob> {
    return this.http.get(`${this.url}/${id}/certificate`, { responseType: 'blob' });
  }

  getCertificateUrl(id: number): string {
    return `${this.url}/${id}/certificate`;
  }
}
