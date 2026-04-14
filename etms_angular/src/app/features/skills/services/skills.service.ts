import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { SkillsRequest, SkillsResponse } from '../models/skill.model';

@Injectable({ providedIn: 'root' })
export class SkillsService {
  private readonly baseUrl = `${environment.apiBaseUrl}/skills`;

  constructor(private http: HttpClient) {}

  getSkills(): Observable<SkillsResponse[]> {
    return this.http.get<SkillsResponse[]>(this.baseUrl);
  }

  getSkillById(skillId: number): Observable<SkillsResponse> {
    return this.http.get<SkillsResponse>(`${this.baseUrl}/${skillId}`);
  }

  createSkill(payload: SkillsRequest): Observable<SkillsResponse> {
    return this.http.post<SkillsResponse>(this.baseUrl, payload);
  }

  updateSkill(skillId: number, payload: SkillsRequest): Observable<SkillsResponse> {
    return this.http.put<SkillsResponse>(`${this.baseUrl}/${skillId}`, payload);
  }

  deleteSkill(skillId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${skillId}`);
  }
}