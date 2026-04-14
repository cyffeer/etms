import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, map, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../../models/api-response.model';

export interface AuthUser {
  loginId?: number | null;
  username: string;
  role?: string | null;
}

interface LoginRequest {
  username: string;
  password: string;
}

interface LoginResponse {
  loginId?: number | null;
  username: string;
  role?: string | null;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly storageKey = 'etms.currentUser';
  private readonly userSubject = new BehaviorSubject<AuthUser | null>(this.readStoredUser());
  readonly user$ = this.userSubject.asObservable();

  constructor(private readonly http: HttpClient) {}

  login(payload: LoginRequest): Observable<AuthUser> {
    return this.http.post<ApiResponse<LoginResponse>>(`${environment.apiBaseUrl}/login`, payload).pipe(
      map((response) => response.data),
      tap((user) => {
        sessionStorage.setItem(this.storageKey, JSON.stringify(user));
        this.userSubject.next(user);
      })
    );
  }

  logout(): void {
    sessionStorage.removeItem(this.storageKey);
    this.userSubject.next(null);
  }

  private readStoredUser(): AuthUser | null {
    const raw = sessionStorage.getItem(this.storageKey);
    if (!raw) {
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
