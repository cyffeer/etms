import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { MemberType, MemberTypeApi, MemberTypeRequest } from '../models/member-type.models';

@Injectable({ providedIn: 'root' })
export class MemberTypeService {
  private readonly baseUrl = `${environment.apiBaseUrl}/member-types`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<MemberType[]> {
    return this.http.get<MemberTypeApi[]>(this.baseUrl);
  }

  getById(id: number): Observable<MemberType> {
    return this.http.get<MemberTypeApi>(`${this.baseUrl}/${id}`);
  }

  create(payload: MemberTypeRequest): Observable<MemberType> {
    return this.http.post<MemberTypeApi>(this.baseUrl, payload);
  }

  update(id: number, payload: MemberTypeRequest): Observable<MemberType> {
    return this.http.put<MemberTypeApi>(`${this.baseUrl}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
