import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../../models/api-response.model';
import { TrngHistRequest, TrngHistResponse } from '../models/training.models';

@Injectable({ providedIn: 'root' })
export class TrngHistService {
  private readonly url = `${environment.apiBaseUrl}/trng-history`;
  constructor(private http: HttpClient) {}
  getAll() { return this.http.get<ApiResponse<TrngHistResponse[]>>(this.url).pipe(map(r => r.data ?? [])); }
  getById(id: number) { return this.http.get<ApiResponse<TrngHistResponse>>(`${this.url}/${id}`).pipe(map(r => r.data)); }
  create(req: TrngHistRequest) { return this.http.post<ApiResponse<TrngHistResponse>>(`${this.url}/assign`, req).pipe(map(r => r.data)); }
  update(id: number, req: TrngHistRequest) { return this.http.put<ApiResponse<TrngHistResponse>>(`${this.url}/${id}`, req).pipe(map(r => r.data)); }
  delete(id: number): Observable<void> { return this.http.delete<ApiResponse<void>>(`${this.url}/${id}`).pipe(map(() => void 0)); }
}
