import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../../models/api-response.model';
import {
  SkillLvlResponse,
  SkillsInventoryRequest,
  SkillsInventoryResponse
} from '../models/skills-inventory.model';

@Injectable({ providedIn: 'root' })
export class SkillsInventoryService {
  private readonly baseUrl = `${environment.apiBaseUrl}/skills-inventory`;
  private readonly skillLevelsUrl = `${environment.apiBaseUrl}/skill-levels`;

  constructor(private http: HttpClient) {}

  getSkillsInventory(): Observable<SkillsInventoryResponse[]> {
    return this.http.get<ApiResponse<SkillsInventoryResponse[]>>(this.baseUrl).pipe(
      map((res) => res.data ?? [])
    );
  }

  getByEmployee(employeeNumber: string): Observable<SkillsInventoryResponse[]> {
    return this.http.get<SkillsInventoryResponse[]>(`${this.baseUrl}/employee/${employeeNumber}/skills`);
  }

  getSkillsInventoryById(skillsInventoryId: number): Observable<SkillsInventoryResponse> {
    return this.http.get<ApiResponse<SkillsInventoryResponse>>(`${this.baseUrl}/${skillsInventoryId}`).pipe(
      map((res) => res.data)
    );
  }

  createSkillsInventory(payload: SkillsInventoryRequest): Observable<SkillsInventoryResponse> {
    return this.http.post<ApiResponse<SkillsInventoryResponse>>(this.baseUrl, payload).pipe(
      map((res) => res.data)
    );
  }

  updateSkillsInventory(skillsInventoryId: number, payload: SkillsInventoryRequest): Observable<SkillsInventoryResponse> {
    return this.http.put<ApiResponse<SkillsInventoryResponse>>(`${this.baseUrl}/${skillsInventoryId}`, payload).pipe(
      map((res) => res.data)
    );
  }

  updateSkillLevel(skillsInventoryId: number, skillLvlId: number): Observable<SkillsInventoryResponse> {
    return this.http.patch<ApiResponse<SkillsInventoryResponse>>(
      `${this.baseUrl}/${skillsInventoryId}/skill-level`,
      { skillLvlId }
    ).pipe(map((res) => res.data));
  }

  deleteSkillsInventory(skillsInventoryId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${skillsInventoryId}`);
  }

  getSkillLevelsBySkill(skillId: number): Observable<SkillLvlResponse[]> {
    return this.http.get<ApiResponse<SkillLvlResponse[]>>(`${this.skillLevelsUrl}/by-skill/${skillId}`).pipe(
      map((res) => res.data ?? [])
    );
  }
}
