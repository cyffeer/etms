import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  constructor(private readonly authService: AuthService, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
      return false;
    }

    const roles = route.data['roles'] as string[] | undefined;
    if (roles?.length && !this.authService.hasAnyRole(roles)) {
      this.router.navigate([this.resolveForbiddenRedirectPath()], { queryParams: { accessDenied: '1' } });
      return false;
    }

    if (route.data['selfOnly'] === true && !this.isSelfAllowed(route)) {
      this.router.navigate([this.resolveForbiddenRedirectPath()], { queryParams: { accessDenied: '1' } });
      return false;
    }

    return true;
  }

  private isSelfAllowed(route: ActivatedRouteSnapshot): boolean {
    const currentUser = this.authService.getCurrentUser();
    if (!currentUser || !this.authService.hasAnyRole(['EMPLOYEE'])) {
      return true;
    }

    const routeValue = this.resolveRouteValue(route);
    if (!routeValue) {
      return false;
    }

    const currentTokens = this.employeeTokens(currentUser.username, currentUser.loginId);
    return currentTokens.some((token) => token === routeValue);
  }

  private resolveRouteValue(route: ActivatedRouteSnapshot): string {
    const values = Object.values(route.params || {});
    const raw = values.find((value) => value !== null && value !== undefined);
    if (raw === undefined || raw === null) {
      return '';
    }
    const text = String(raw).trim().toLowerCase();
    return text.replace(/[^a-z0-9]/g, '');
  }

  private employeeTokens(username: string, loginId?: number | null): string[] {
    const tokens = new Set<string>();
    const normalizedUsername = (username || '').trim().toLowerCase().replace(/[^a-z0-9]/g, '');
    if (normalizedUsername) {
      tokens.add(normalizedUsername);
      const digits = normalizedUsername.replace(/\D+/g, '');
      if (digits) {
        tokens.add(digits.replace(/^0+/, '') || '0');
        tokens.add(`emp${digits.replace(/^0+/, '') || '0'}`);
      }
    }
    if (loginId !== null && loginId !== undefined) {
      tokens.add(String(loginId));
    }
    return [...tokens];
  }

  private resolveForbiddenRedirectPath(): string {
    const currentUser = this.authService.getCurrentUser();
    return (currentUser?.role || '').toUpperCase() === 'EMPLOYEE' ? '/employee/profile' : '/dashboard';
  }
}
