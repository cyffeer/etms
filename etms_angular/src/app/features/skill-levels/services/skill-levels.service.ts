import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../../models/api-response.model';
import { SkillLevelListResult, SkillLevelPagedData, SkillLevelRequest, SkillLevelResponse } from '../models/skill-level.model';

@Injectable({ providedIn: 'root' })
export class SkillLevelsService {
  private readonly baseUrl = `${environment.apiBaseUrl}/skill-levels`;

  constructor(private http: HttpClient) {}

  getSkillLevels(filters?: {
    skillLvlId?: number | null;
    skillId?: number | null;
    keyword?: string | null;
  }, page?: number, size?: number): Observable<SkillLevelListResult> {
    let params = new HttpParams();

    if (filters?.skillLvlId != null) {
      params = params.set('skillLvlId', filters.skillLvlId);
    }
    if (filters?.skillId != null) {
      params = params.set('skillId', filters.skillId);
    }
    if (filters?.keyword?.trim()) {
      params = params.set('keyword', filters.keyword.trim());
    }
    if (page != null && size != null) {
      params = params.set('page', page).set('size', size);
    }

    return this.http
      .get<ApiResponse<SkillLevelResponse[] | SkillLevelPagedData>>(this.baseUrl, { params })
      .pipe(map((res) => {
        const payload = res.data;
        if (Array.isArray(payload)) {
          return { items: payload };
        }
        return {
          items: payload.data,
          page: payload.page,
          size: payload.size,
          totalElements: payload.totalElements,
          totalPages: payload.totalPages
        };
      }));
  }

  getSkillLevelById(skillLvlId: number): Observable<SkillLevelResponse> {
    return this.http
      .get<ApiResponse<SkillLevelResponse>>(`${this.baseUrl}/${skillLvlId}`)
      .pipe(map((res) => res.data));
  }

  createSkillLevel(payload: SkillLevelRequest): Observable<SkillLevelResponse> {
    return this.http
      .post<ApiResponse<SkillLevelResponse>>(this.baseUrl, payload)
      .pipe(map((res) => res.data));
  }

  updateSkillLevel(skillLvlId: number, payload: SkillLevelRequest): Observable<SkillLevelResponse> {
    return this.http
      .put<ApiResponse<SkillLevelResponse>>(`${this.baseUrl}/${skillLvlId}`, payload)
      .pipe(map((res) => res.data));
  }

  deleteSkillLevel(skillLvlId: number): Observable<void> {
    return this.http
      .delete<ApiResponse<void>>(`${this.baseUrl}/${skillLvlId}`)
      .pipe(map(() => void 0));
  }
}
