import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { Vendor, VendorRequest } from '../models/training.models';

@Injectable({ providedIn: 'root' })
export class VendorService {
  private url = `${environment.apiBaseUrl}/vendors`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<Vendor[]> {
    return this.http.get<Vendor[]>(this.url);
  }

  getById(id: number): Observable<Vendor> {
    return this.http.get<Vendor>(`${this.url}/${id}`);
  }

  create(req: VendorRequest): Observable<Vendor> {
    return this.http.post<Vendor>(this.url, req);
  }

  update(id: number, req: VendorRequest): Observable<Vendor> {
    return this.http.put<Vendor>(`${this.url}/${id}`, req);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
