import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { NpTestHist } from '../models/nihongo.models';

@Injectable({ providedIn: 'root' })
export class NpTestHistService {
  private readonly baseUrl = `${environment.apiBaseUrl}/np-test-hist`;

  constructor(private readonly http: HttpClient) {}

  getAll(): Observable<NpTestHist[]> {
    return this.http.get<NpTestHist[]>(this.baseUrl);
  }
}
