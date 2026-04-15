import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, map, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../../models/api-response.model';

export interface AuthUser {
  loginId?: number | null;
  username: string;
  role?: string | null;
  tokenType?: string | null;
  expiresInSeconds?: number | null;
}

interface LoginRequest {
  username: string;
  password: string;
}

interface LoginResponse {
  loginId?: number | null;
  username: string;
  role?: string | null;
  accessToken: string;
  tokenType?: string | null;
  expiresInSeconds?: number | null;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly storageKey = 'etms.currentUser';
  private readonly tokenStorageKey = 'etms.currentAuthToken';
  private readonly userSubject = new BehaviorSubject<AuthUser | null>(this.readStoredUser());
  readonly user$ = this.userSubject.asObservable();

  constructor(private readonly http: HttpClient) {}

  login(payload: LoginRequest): Observable<AuthUser> {
    return this.http.post<ApiResponse<LoginResponse>>(`${environment.apiBaseUrl}/login`, payload).pipe(
      map((response) => response.data),
      tap((loginResponse) => {
        const user: AuthUser = {
          loginId: loginResponse.loginId,
          username: loginResponse.username,
          role: loginResponse.role,
          tokenType: loginResponse.tokenType || 'Bearer',
          expiresInSeconds: loginResponse.expiresInSeconds ?? null
        };

        sessionStorage.setItem(this.storageKey, JSON.stringify(user));
        sessionStorage.setItem(this.tokenStorageKey, loginResponse.accessToken);
        this.userSubject.next(user);
      })
    );
  }

  logout(): void {
    sessionStorage.removeItem(this.storageKey);
    sessionStorage.removeItem(this.tokenStorageKey);
    this.userSubject.next(null);
  }

  isAuthenticated(): boolean {
    return !!this.userSubject.value && !!sessionStorage.getItem(this.tokenStorageKey);
  }

  hasAnyRole(roles: string[]): boolean {
    if (!roles.length) {
      return true;
    }
    const currentRole = (this.userSubject.value?.role || '').toUpperCase();
    return !!currentRole && roles.some((role) => role.toUpperCase() === currentRole);
  }

  getCurrentUser(): AuthUser | null {
    return this.userSubject.value;
  }

  getAuthorizationHeader(): string | null {
    const token = sessionStorage.getItem(this.tokenStorageKey);
    return token ? `Bearer ${token}` : null;
  }

  private readStoredUser(): AuthUser | null {
    const raw = sessionStorage.getItem(this.storageKey);
    const token = sessionStorage.getItem(this.tokenStorageKey);
    if (!raw || !token) {
      sessionStorage.removeItem(this.storageKey);
      sessionStorage.removeItem(this.tokenStorageKey);
      return null;
    }

    try {
      return JSON.parse(raw) as AuthUser;
    } catch {
      sessionStorage.removeItem(this.storageKey);
      return null;
    }
  }

}
