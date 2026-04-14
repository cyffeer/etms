import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { TrngType, TrngTypeRequest } from '../models/training.models';

@Injectable({ providedIn: 'root' })
export class TrngTypeService {
  private url = `${environment.apiBaseUrl}/trng-types`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<TrngType[]> {
    return this.http.get<TrngType[]>(this.url);
  }

  getById(id: number): Observable<TrngType> {
    return this.http.get<TrngType>(`${this.url}/${id}`);
  }

  create(req: TrngTypeRequest): Observable<TrngType> {
    return this.http.post<TrngType>(this.url, req);
  }

  update(id: number, req: TrngTypeRequest): Observable<TrngType> {
    return this.http.put<TrngType>(`${this.url}/${id}`, req);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
