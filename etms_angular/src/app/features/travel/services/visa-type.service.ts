import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { VisaType, VisaTypeRequest } from '../models/travel.models';

@Injectable({ providedIn: 'root' })
export class VisaTypeService {
  private readonly baseUrl = `${environment.apiBaseUrl}/visa-types`;

  constructor(private readonly http: HttpClient) {}

  getAll(): Observable<VisaType[]> {
    return this.http.get<VisaType[]>(this.baseUrl);
  }

  getById(id: number): Observable<VisaType> {
    return this.http.get<VisaType>(`${this.baseUrl}/${id}`);
  }

  create(payload: VisaTypeRequest): Observable<VisaType> {
    return this.http.post<VisaType>(this.baseUrl, payload);
  }

  update(id: number, payload: VisaTypeRequest): Observable<VisaType> {
    return this.http.put<VisaType>(`${this.baseUrl}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
